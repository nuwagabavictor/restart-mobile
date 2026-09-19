package com.victor.restart.feature.budget

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("budget")
data object BudgetRoute

@Serializable
@SerialName("budget_form")
data class BudgetFormRoute(
    val budgetId: Long? = null,
    val categoryId: Long? = null

)

fun NavController.navigateToBudgets() {
    navigate(BudgetRoute) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToBudgetForm(budgetId: Long? = null, categoryId: Long? = null) {
    navigate(BudgetFormRoute(budgetId, categoryId))
}

fun NavGraphBuilder.budgetDestination(
    navController: NavController
) {

    composable<BudgetRoute> {
        BudgetScreen(
            navigateToBudgetForm = {
                navController.navigateToBudgetForm()
            },
            navigateToEditBudget = { id ->
                navController.navigateToBudgetForm(id)
            },
            navController = navController,
            startInFormMode = false
        )
    }

    composable<BudgetFormRoute> {

        val route = it.toRoute<BudgetFormRoute>()

        BudgetFormScreen(
            budgetId = route.budgetId,
            categoryId = route.categoryId,
            navController = navController
        )
    }
}