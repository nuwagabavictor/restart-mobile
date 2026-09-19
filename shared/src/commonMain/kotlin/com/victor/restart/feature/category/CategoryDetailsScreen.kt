package com.victor.restart.feature.category


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.entity.Category
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryDetailsScreen(
    categoryId: Long,
    navigateBack: () -> Unit,
    navigateToAddTransaction: (Long) -> Unit,
    navigateToAddBudget: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(categoryId) {
        viewModel.trySendAction(
            CategoryAction.LoadCategory(categoryId)
        )
    }

    val category = state.selectedCategory

    // Loading
    if (state.showOverlay && category == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    // Category not found
    if (category == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Category not found",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Category ID: $categoryId",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = navigateBack
                ) {
                    Text("Go Back")
                }
            }
        }

        return
    }

    // Category details
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(category.name)
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            CategoryDetailsCard(
                category = category
            )

            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navigateToAddTransaction(categoryId)
                    }
                ) {
                    Text("Add Transaction")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navigateToAddBudget(categoryId)
                    }
                ) {
                    Text("Add Budget")
                }
            }
        }
    }
}

@Composable
private fun CategoryDetailsCard(
    category: Category
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = category.name,
                style = MaterialTheme.typography.headlineSmall
            )

            if (!category.description.isNullOrBlank()) {
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Type: ${
                    category.type.name
                        .lowercase()
                        .replace('_', ' ')
                        .replaceFirstChar { it.uppercase() }
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (category.active) {
                    "Status: Active"
                } else {
                    "Status: Inactive"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}