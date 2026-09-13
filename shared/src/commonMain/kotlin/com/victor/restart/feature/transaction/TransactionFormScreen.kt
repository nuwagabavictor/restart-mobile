package com.victor.restart.feature.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_transaction_amount
import restart.shared.generated.resources.feature_transaction_category
import restart.shared.generated.resources.feature_transaction_create_title
import restart.shared.generated.resources.feature_transaction_description
import restart.shared.generated.resources.feature_transaction_edit_title
import restart.shared.generated.resources.feature_transaction_save
import restart.shared.generated.resources.feature_transaction_update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    // Handle save / back events from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TransactionEvent.NavigateBack -> {
                    // Tell the list screen to refresh
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_transactions", true)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("home_needs_refresh", true)
                    navController.popBackStack()
                }
                is TransactionEvent.ShowToast -> {
                    // Optional: hook into your snackbar host
                }
                is TransactionEvent.NavigateToEdit -> {
                    // Not used from the form screen
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditMode) Res.string.feature_transaction_edit_title
                            else Res.string.feature_transaction_create_title
                        )
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {

            // ---------- Category dropdown ----------
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    value = state.selectedCategory?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(stringResource(Res.string.feature_transaction_category))
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (state.categories.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No categories") },
                            onClick = { expanded = false }
                        )
                    } else {
                        state.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.trySendAction(
                                        TransactionAction.CategoryChanged(category)
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            state.categoryError?.let {
                Text(
                    stringResource(it),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Amount ----------
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.amount,
                onValueChange = {
                    viewModel.trySendAction(TransactionAction.AmountChanged(it))
                },
                label = {
                    Text(stringResource(Res.string.feature_transaction_amount))
                }
            )

            state.amountError?.let {
                Text(
                    stringResource(it),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Description ----------
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {
                    viewModel.trySendAction(TransactionAction.DescriptionChanged(it))
                },
                label = {
                    Text(stringResource(Res.string.feature_transaction_description))
                }
            )

            Spacer(Modifier.height(24.dp))

            // ---------- Save / Update ----------
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveButtonEnabled,
                onClick = {
                    viewModel.trySendAction(TransactionAction.SaveTransactionClicked)
                }
            ) {
                Text(
                    stringResource(
                        if (state.isEditMode) Res.string.feature_transaction_update
                        else Res.string.feature_transaction_save
                    )
                )
            }
        }
    }

    if (state.showOverlay) LoadingOverlay()
    TransactionDialogs(state, viewModel)
}