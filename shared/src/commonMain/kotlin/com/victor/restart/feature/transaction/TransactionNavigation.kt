package com.victor.restart.feature.transaction


import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("transaction")
data object TransactionRoute

@Serializable
@SerialName("transaction_form")
data class TransactionFormRoute(
    val transactionId: Long? = null
)

fun NavController.navigateToTransactions() {
    navigate(TransactionRoute) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToTransactionForm(transactionId: Long? = null) {
    navigate(TransactionFormRoute(transactionId))
}

fun NavGraphBuilder.transactionDestination(
    navController: NavController
) {
    composable<TransactionRoute> {
        TransactionScreen(
            navigateToTransactionForm = { navController.navigateToTransactionForm() },
            navigateToEditTransaction = { id -> navController.navigateToTransactionForm(id) },
            navController = navController,
        )
    }

    composable<TransactionFormRoute> {
        TransactionFormScreen(
            navController = navController,
        )
    }
}