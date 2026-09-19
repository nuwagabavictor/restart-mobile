package com.victor.restart.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.victor.restart.feature.category.navigateToCategories
import com.victor.restart.feature.transaction.navigateToTransactions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data object HomeGraph
@Serializable
@SerialName("home")
data object HomeRoute

fun NavController.navigateToHome() {
    navigate(HomeRoute) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.homeDestination(
    navController: NavController
) {navigation<HomeGraph>(
    startDestination = HomeRoute
) {
    composable<HomeRoute> {
        HomeScreen(
            navigateToTransactions = { navController.navigateToTransactions() },
            navigateToCategories = { navController.navigateToCategories() },
            navController = navController
        )
    }
}
}