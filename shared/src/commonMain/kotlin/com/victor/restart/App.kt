package com.victor.restart



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.repository.userdata.UserDataRepository
import com.victor.restart.feature.category.CategoryRoute
import com.victor.restart.feature.category.categoryDestination
import com.victor.restart.feature.login.loginDestination
import com.victor.restart.feature.login.navigateToLoginScreen
import com.victor.restart.feature.password.navigateToPassword
import com.victor.restart.feature.password.passwordDestination
import com.victor.restart.feature.register.RegisterRoute
import com.victor.restart.feature.register.navigateToRegisterScreen
import com.victor.restart.feature.register.registerDestination
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
            navigateToCategoryScreen = { /* no-op — gate handles it */ },
            navigateToForgotPasswordScreen = { navController.navigateToPassword() },
        )

        passwordDestination(
            navigateToCancel = { navController.popBackStack() }
        )
    }
}

@Composable
private fun PrivateNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CategoryRoute,
    ) {
        categoryDestination(navController)
    }
}
