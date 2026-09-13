package com.victor.restart.feature.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.victor.restart.core.entity.Transaction
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.error_title
import restart.shared.generated.resources.feature_transaction_add
import restart.shared.generated.resources.feature_transaction_count
import restart.shared.generated.resources.feature_transaction_create_hint
import restart.shared.generated.resources.feature_transaction_edit
import restart.shared.generated.resources.feature_transaction_no_results
import restart.shared.generated.resources.feature_transaction_no_transactions
import restart.shared.generated.resources.feature_transaction_search
import restart.shared.generated.resources.feature_transaction_search_hint
import restart.shared.generated.resources.feature_transaction_title
import restart.shared.generated.resources.ok

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navigateToTransactionForm: () -> Unit,
    navigateToEditTransaction: (Long) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    // Refresh the list whenever the form screen signals completion
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("refresh_transactions", false)
            ?.collect { refresh ->
                if (refresh) {
                    viewModel.trySendAction(TransactionAction.LoadTransactions)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_transactions", false)
                }
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.feature_transaction_title))
                        Text(
                            stringResource(
                                Res.string.feature_transaction_count,
                                state.transactions.size
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToTransactionForm) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.searchQuery,
                onValueChange = {
                    viewModel.trySendAction(TransactionAction.SearchQueryChanged(it))
                },
                singleLine = true,
                placeholder = {
                    Text(stringResource(Res.string.feature_transaction_search))
                }
            )

            Spacer(Modifier.height(16.dp))

            TransactionContent(
                transactions = state.filteredTransactions,
                searchQuery = state.searchQuery,
                onAddTransaction = navigateToTransactionForm,
                onEditTransaction = navigateToEditTransaction,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (state.showOverlay) LoadingOverlay()
    TransactionDialogs(state, viewModel)
}

@Composable
fun TransactionContent(
    transactions: List<Transaction>,
    searchQuery: String,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactions.isEmpty()) {
        EmptyTransactionContent(
            hasSearchQuery = searchQuery.isNotBlank(),
            onAddTransaction = onAddTransaction,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = transactions, key = { it.id }) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onEdit = { onEditTransaction(transaction.id) }
                )
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initial = transaction.categoryName
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString()
                ?: "?"

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    transaction.transactionType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!transaction.description.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        transaction.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    transaction.transactionDate.toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = transaction.amount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onEdit) {
                    Text(stringResource(Res.string.feature_transaction_edit))
                }
            }
        }
    }
}

@Composable
fun EmptyTransactionContent(
    hasSearchQuery: Boolean,
    onAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "T",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (hasSearchQuery)
                    stringResource(Res.string.feature_transaction_no_results)
                else
                    stringResource(Res.string.feature_transaction_no_transactions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (hasSearchQuery)
                    stringResource(Res.string.feature_transaction_search_hint)
                else
                    stringResource(Res.string.feature_transaction_create_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!hasSearchQuery) {
                Spacer(Modifier.height(20.dp))
                TextButton(onClick = onAddTransaction) {
                    Text(stringResource(Res.string.feature_transaction_add))
                }
            }
        }
    }
}

@Composable
fun TransactionDialogs(
    state: TransactionState,
    viewModel: TransactionViewModel
) {
    when (val dialog = state.dialogState) {
        is TransactionState.DialogState.Error -> {
            Dialog(
                onDismissRequest = {
                    viewModel.trySendAction(TransactionAction.ErrorDialogDismiss)
                }
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.error_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = dialog.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.trySendAction(TransactionAction.ErrorDialogDismiss)
                                }
                            ) {
                                Text(stringResource(Res.string.ok))
                            }
                        }
                    }
                }
            }
        }
        null -> Unit
    }
}

@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}