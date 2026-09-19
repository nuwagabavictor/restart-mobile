package com.victor.restart.feature.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import org.jetbrains.compose.resources.stringResource
import restart.shared.generated.resources.Res
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import restart.shared.generated.resources.feature_category_add
import restart.shared.generated.resources.feature_category_create_hint
import restart.shared.generated.resources.feature_category_no_categories
import restart.shared.generated.resources.feature_category_no_results
import restart.shared.generated.resources.feature_category_search_hint

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.ui.text.style.TextOverflow
import com.victor.restart.core.entity.Category
import restart.shared.generated.resources.feature_category_edit

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.victor.restart.core.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.feature_category_count
import restart.shared.generated.resources.feature_category_delete_confirm
import restart.shared.generated.resources.feature_category_search
import restart.shared.generated.resources.feature_category_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navigateToCategoryForm: () -> Unit,
    navigateToEditCategory: (Long) -> Unit,
    navigateToCategory: (Long) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CategoryViewModel = koinViewModel()
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is CategoryEvent.NavigateToCategory -> {
                navigateToCategory(event.categoryId)
            }

            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("refresh_categories", false)
            ?.collect { refresh ->
                if (refresh) {
                    viewModel.trySendAction(CategoryAction.LoadCategories)

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_categories", false)
                }
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(
                                Res.string.feature_category_title
                            )
                        )

                        Text(
                            text = stringResource(
                                Res.string.feature_category_count
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
                onClick = navigateToCategoryForm
            ) {
                Text("+")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.trySendAction(
                        CategoryAction.SearchQueryChanged(it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        stringResource(
                            Res.string.feature_category_search
                        )
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            CategoryContent(
                categories = state.filteredCategories,
                searchQuery = state.searchQuery,
                onCategoryClick = {
                    viewModel.trySendAction(
                    CategoryAction.CategoryClicked(it)
                )},
                onAddCategory = navigateToCategoryForm,
                onEditCategory = navigateToEditCategory,
                onDeleteCategory = {
                    viewModel.trySendAction(
                        CategoryAction.DeleteCategoryClicked(it)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (state.showOverlay) {
        LoadingOverlay()
    }

}


@Composable
fun CategoryContent(
    categories: List<Category>,
    searchQuery: String,
    onCategoryClick: (Long) -> Unit,
    onAddCategory: () -> Unit,
    onEditCategory: (Long) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) {

        EmptyCategoryContent(
            hasSearchQuery = searchQuery.isNotBlank(),
            onAddCategory = onAddCategory,
            modifier = modifier
        )

    } else {

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = categories,
                key = { it.id }
            ) { category ->

                CategoryCard(
                    category = category,
                    onEdit = {
                        onEditCategory(category.id)
                    },
                    onDelete = {
                        onDeleteCategory(category)
                    },
                    onClick = {
                        onCategoryClick(category.id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!category.description.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                category.type.name
                                    .lowercase()
                                    .replaceFirstChar {
                                        it.uppercase()
                                    }
                            )
                        }
                    )

                    CategoryStatusBadge(
                        isActive = category.active
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                TextButton(
                    onClick = onEdit
                ) {
                    Text(
                        stringResource(
                            Res.string.feature_category_edit
                        )
                    )
                }

                TextButton(
                    onClick = onDelete
                ) {
                    Text(
                        stringResource(
                            Res.string.feature_category_delete_confirm
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStatusBadge(
    isActive: Boolean
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isActive) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Text(
            text = if (isActive) "Active" else "Inactive",
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun EmptyCategoryContent(
    hasSearchQuery: Boolean,
    onAddCategory: () -> Unit,
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
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = if (hasSearchQuery) {
                    stringResource(
                        Res.string.feature_category_no_results
                    )
                } else {
                    stringResource(
                        Res.string.feature_category_no_categories
                    )
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (hasSearchQuery) {
                    stringResource(
                        Res.string.feature_category_search_hint
                    )
                } else {
                    stringResource(
                        Res.string.feature_category_create_hint
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!hasSearchQuery) {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                TextButton(
                    onClick = onAddCategory
                ) {
                    Text(
                        stringResource(
                            Res.string.feature_category_add
                        )
                    )
                }
            }
        }
    }
}



@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.7f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
