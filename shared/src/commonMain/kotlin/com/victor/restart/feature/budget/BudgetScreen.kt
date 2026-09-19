package com.victor.restart.feature.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.victor.restart.core.entity.Budget
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navigateToBudgetForm: () -> Unit,
    navigateToEditBudget: (Long) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    startInFormMode: Boolean = false,
    viewModel: BudgetViewModel = koinViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.feature_budget_title))

                        Text(
                            stringResource(
                                Res.string.feature_budget_count,
                                state.budgets.size
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToBudgetForm
            ) {
                Text(
                    "+",
                    style = MaterialTheme.typography.headlineSmall
                )
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
                    viewModel.trySendAction(
                        BudgetAction.SearchQueryChanged(it)
                    )
                },
                singleLine = true,
                placeholder = {
                    Text(
                        stringResource(
                            Res.string.feature_budget_search
                        )
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            BudgetContent(
                budgets = state.filteredBudgets,
                searchQuery = state.searchQuery,
                onAddBudget = navigateToBudgetForm,
                onEditBudget = navigateToEditBudget,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (state.showOverlay) {
        BudgetLoadingOverlay()
    }

    BudgetDialogs(
        state = state,
        viewModel = viewModel
    )
}

@Composable
fun BudgetContent(
    budgets: List<Budget>,
    searchQuery: String,
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (budgets.isEmpty()) {

        EmptyBudgetContent(
            hasSearchQuery = searchQuery.isNotBlank(),
            onAddBudget = onAddBudget,
            modifier = modifier
        )

    } else {

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = budgets,
                key = { it.id }
            ) { budget ->

                BudgetCard(
                    budget = budget,
                    onEdit = {
                        onEditBudget(budget.id)
                    }
                )
            }
        }
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        budget.categoryName.firstOrNull()?.uppercase() ?: "?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        budget.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        budget.period.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onEdit) {
                    Text(stringResource(Res.string.feature_transaction_edit))
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Budget",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                "UGX ${budget.amount.toInt()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = {
                    (budget.amountSpent / budget.amount)
                        .coerceIn(0.0, 1.0)
                        .toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        stringResource(Res.string.feature_budget_spent),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        "UGX ${budget.amountSpent.toInt()}",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        stringResource(Res.string.feature_budget_remaining),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        "UGX ${(budget.amount - budget.amountSpent).coerceAtLeast(0.0).toInt()}",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (budget.isBudgetExceeded) {

                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            stringResource(Res.string.feature_budget_exceeded),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            "UGX ${budget.exceededAmount.toInt()}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

            } else {

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        text = stringResource(Res.string.feature_budget_active),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Start: ${budget.startDate}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "End: ${budget.endDate}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyBudgetContent(
    hasSearchQuery: Boolean,
    onAddBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "B",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text =
                    if (hasSearchQuery)
                        stringResource(Res.string.feature_budget_no_results)
                    else
                        stringResource(Res.string.feature_budget_no_budgets),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    if (hasSearchQuery)
                        stringResource(Res.string.feature_budget_search_hint)
                    else
                        stringResource(Res.string.feature_budget_create_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!hasSearchQuery) {

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onAddBudget
                ) {
                    Text(
                        stringResource(Res.string.feature_budget_add)
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetDialogs(
    state: BudgetState,
    viewModel: BudgetViewModel
) {
    when (val dialog = state.dialogState) {

        is BudgetState.DialogState.Error -> {

            Dialog(
                onDismissRequest = {
                    viewModel.trySendAction(
                        BudgetAction.ErrorDialogDismiss
                    )
                }
            ) {

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 6.dp
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Text(
                            stringResource(Res.string.error_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(dialog.message)

                        Spacer(Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            TextButton(
                                onClick = {
                                    viewModel.trySendAction(
                                        BudgetAction.ErrorDialogDismiss
                                    )
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
fun BudgetLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}