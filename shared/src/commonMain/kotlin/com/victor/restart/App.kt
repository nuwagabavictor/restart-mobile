package com.victor.restart



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.repository.userdata.UserDataRepository
import com.victor.restart.feature.category.CategoryRoute
import com.victor.restart.feature.category.categoryDestination
import com.victor.restart.feature.category.navigateToCategories
import com.victor.restart.feature.home.BottomNavItem
import com.victor.restart.feature.home.HomeRoute
import com.victor.restart.feature.home.homeDestination
import com.victor.restart.feature.home.navigateToHome
import com.victor.restart.feature.login.loginDestination
import com.victor.restart.feature.login.navigateToLoginScreen
import com.victor.restart.feature.password.navigateToPassword
import com.victor.restart.feature.password.passwordDestination
import com.victor.restart.feature.register.RegisterRoute
import com.victor.restart.feature.register.navigateToRegisterScreen
import com.victor.restart.feature.register.registerDestination
import com.victor.restart.feature.transaction.TransactionRoute
import com.victor.restart.feature.transaction.navigateToTransactions
import com.victor.restart.feature.transaction.transactionDestination
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Single shared entry point for the whole app's UI. Both MainActivity (Android)
 * and MainViewController (iOS) call this — it's the KMP equivalent of a shared
 * "root" servlet/controller that every platform-specific launcher delegates to.
 */
@Composable
fun App() {
    MaterialTheme {
        Surface {
            val repository: UserDataRepository = koinInject()
            val authState by repository.authState.collectAsStateWithLifecycle(initialValue = AuthState.Loading)

            when (authState) {
                AuthState.Loading -> LoadingScreen()
                AuthState.Unauthenticated -> PublicNavGraph()
                is AuthState.Authenticated -> PrivateNavGraph()
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PublicNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RegisterRoute,
    ) {
        registerDestination(
            navigateToLoginScreen = { navController.navigateToLoginScreen() }
        )

        loginDestination(
            navigateToRegisterScreen = { navController.navigateToRegisterScreen() },
            // NOTE: after a successful login, the repository flips
            // isAuthenticated = true. We do NOT navigate here. The gate
            // will replace this whole graph with the private one.
            navigateToHomeScreen = { /* no-op — gate handles it */ },
            navigateToForgotPasswordScreen = { navController.navigateToPassword() },
        )

        passwordDestination(
            navigateToCancel = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivateNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Bar visible only on the three top-level tab routes
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        dest.hasRoute(HomeRoute::class) ||
                dest.hasRoute(CategoryRoute::class) ||
                dest.hasRoute(TransactionRoute::class)
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        val selected = when (item) {
                            BottomNavItem.Home ->
                                currentDestination.hierarchy.any { it.hasRoute(HomeRoute::class) }
                            BottomNavItem.Categories ->
                                currentDestination.hierarchy.any { it.hasRoute(CategoryRoute::class) }
                            BottomNavItem.Transactions ->
                                currentDestination.hierarchy.any { it.hasRoute(TransactionRoute::class) }
                        }

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (selected) return@NavigationBarItem
                                when (item) {
                                    BottomNavItem.Home -> navController.navigateToHome()
                                    BottomNavItem.Categories -> navController.navigateToCategories()
                                    BottomNavItem.Transactions -> navController.navigateToTransactions()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.label)
                                )
                            },
                            label = { Text(stringResource(item.label)) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding)
        ) {
            homeDestination(navController)
            categoryDestination(navController)
            transactionDestination(navController)
        }
    }
}
