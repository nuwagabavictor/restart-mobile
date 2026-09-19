package com.victor.restart.feature.transaction


import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("transaction_graph")
data object TransactionGraph

@Serializable
@SerialName("transaction")
data object TransactionRoute

@Serializable
@SerialName("transaction_form")
data class TransactionFormRoute(
    val transactionId: Long? = null,
    val categoryId: Long? = null

)

fun NavController.navigateToTransactions() {
    navigate(TransactionRoute) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToTransactionForm(transactionId: Long? = null, categoryId: Long? = null
) {
    navigate(TransactionFormRoute(
        transactionId, categoryId
    ))
}

fun NavGraphBuilder.transactionDestination(
    navController: NavController
) {
    navigation<TransactionGraph>(
        startDestination = TransactionRoute
    ) {
        composable<TransactionRoute> {
            TransactionScreen(
                navigateToTransactionForm = { navController.navigateToTransactionForm() },
                navigateToEditTransaction = { id -> navController.navigateToTransactionForm(id) },
                navController = navController,
            )
        }

        composable<TransactionFormRoute> {
            val route = it.toRoute<TransactionFormRoute>()

            TransactionFormScreen(
                transactionId = route.transactionId,
                categoryId = route.categoryId,
                navController = navController,
            )
        }
    }
}