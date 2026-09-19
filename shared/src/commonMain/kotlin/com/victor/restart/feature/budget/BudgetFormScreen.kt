package com.victor.restart.feature.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.victor.restart.core.entity.BudgetPeriod
import restart.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFormScreen(
    budgetId: Long?,
    categoryId: Long?,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BudgetViewModel = koinViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditMode)
                                Res.string.feature_budget_edit_title
                            else
                                Res.string.feature_budget_create_title
                        )
                    )
                }
            )
        }
    ) { padding ->

        BudgetFormContent(
            state = state,
            onAction = viewModel::trySendAction,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        )
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
fun BudgetFormContent(
    state: BudgetState,
    onAction: (BudgetAction) -> Unit,
    modifier: Modifier = Modifier
) {

    var periodExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // ---------------- CATEGORY ----------------

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.selectedCategory?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = {
                Text(
                    stringResource(
                        Res.string.feature_budget_category
                    )
                )
            }
        )

        state.categoryError?.let {
            Text(
                stringResource(it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ---------------- AMOUNT ----------------

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.amount,
            onValueChange = {
                onAction(
                    BudgetAction.AmountChanged(it)
                )
            },
            label = {
                Text(
                    stringResource(
                        Res.string.feature_budget_amount
                    )
                )
            },
            singleLine = true
        )

        state.amountError?.let {
            Text(
                stringResource(it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ---------------- PERIOD ----------------

        Box {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        periodExpanded = true
                    },
                value = when (state.period) {

                    BudgetPeriod.WEEKLY ->
                        stringResource(
                            Res.string.feature_budget_period_weekly
                        )

                    BudgetPeriod.MONTHLY ->
                        stringResource(
                            Res.string.feature_budget_period_monthly
                        )

                    BudgetPeriod.YEARLY ->
                        stringResource(
                            Res.string.feature_budget_period_yearly
                        )
                },
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        stringResource(
                            Res.string.feature_budget_period
                        )
                    )
                }
            )

            DropdownMenu(
                expanded = periodExpanded,
                onDismissRequest = {
                    periodExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                Res.string.feature_budget_period_weekly
                            )
                        )
                    },
                    onClick = {
                        onAction(
                            BudgetAction.PeriodChanged(
                                BudgetPeriod.WEEKLY
                            )
                        )

                        periodExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                Res.string.feature_budget_period_monthly
                            )
                        )
                    },
                    onClick = {
                        onAction(
                            BudgetAction.PeriodChanged(
                                BudgetPeriod.MONTHLY
                            )
                        )

                        periodExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                Res.string.feature_budget_period_yearly
                            )
                        )
                    },
                    onClick = {
                        onAction(
                            BudgetAction.PeriodChanged(
                                BudgetPeriod.YEARLY
                            )
                        )

                        periodExpanded = false
                    }
                )
            }
        }

        state.periodError?.let {
            Text(
                stringResource(it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ---------------- SAVE BUTTON ----------------

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSaveButtonEnabled,
            onClick = {
                onAction(
                    BudgetAction.SaveBudgetClicked
                )
            }
        ) {
            Text(
                stringResource(
                    if (state.isEditMode)
                        Res.string.feature_budget_update
                    else
                        Res.string.feature_budget_save
                )
            )
        }
    }
}