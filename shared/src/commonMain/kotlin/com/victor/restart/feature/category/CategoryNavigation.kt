package com.victor.restart.feature.category

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.victor.restart.feature.budget.BudgetFormRoute
import com.victor.restart.feature.budget.BudgetFormScreen
import com.victor.restart.feature.budget.navigateToBudgetForm
import com.victor.restart.feature.transaction.TransactionRoute
import com.victor.restart.feature.transaction.navigateToTransactionForm
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("category_graph")
data object CategoryGraph
@Serializable
@SerialName("category")
data object CategoryRoute

@Serializable
@SerialName("category_form")
data class CategoryFormRoute(
    val categoryId: Long? = null
)

@Serializable
@SerialName("category_details")
data class CategoryDetailsRoute(
    val categoryId: Long
)

fun NavController.navigateToCategoryForm(categoryId: Long? = null) {
    navigate(CategoryFormRoute(categoryId))
}

fun NavController.navigateToCategoryDetails(categoryId: Long) {
    navigate(CategoryDetailsRoute(categoryId))
}

fun NavController.navigateToCategories() {
    navigate(CategoryRoute){
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.categoryDestination(
    navController: NavController
) {
    navigation<CategoryGraph>(
        startDestination = CategoryRoute
    ) {
        composable<CategoryRoute> {
            CategoryScreen(
                navigateToCategoryForm = { navController.navigateToCategoryForm() },
                navigateToEditCategory = { id -> navController.navigateToCategoryForm(id) },
                navigateToCategory = { id -> navController.navigateToCategoryDetails(id) },
                navController = navController
            )
        }

        composable<CategoryFormRoute> {
            CategoryFormScreen(
                navigateBack = { navController.popBackStack() },
                navController = navController

            )
        }

        composable<CategoryDetailsRoute> {
            CategoryDetailsScreen(
                categoryId = it.toRoute<CategoryDetailsRoute>().categoryId,

                navigateBack = {
                    navController.popBackStack()
                },
                navigateToAddTransaction = { categoryId ->
                    navController.navigateToTransactionForm(
                        categoryId = categoryId
                    )
                },
                navigateToAddBudget = { categoryId ->
                    navController.navigateToBudgetForm(
                        categoryId = categoryId
                    )
                },
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
}