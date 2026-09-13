package com.victor.restart.feature.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.victor.restart.core.entity.CategoryType
import com.victor.restart.core.utils.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_category_create
import restart.shared.generated.resources.feature_category_description
import restart.shared.generated.resources.feature_category_edit_cat
import restart.shared.generated.resources.feature_category_name

private val ScreenPadding = 16.dp
private val FieldSpacing = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    viewModel: CategoryViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CategoryEvent.NavigateBack -> {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("refresh_categories", true)

                navigateBack()
            }
            is CategoryEvent.ShowToast -> Unit
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode) {
                            stringResource(Res.string.feature_category_edit_cat)
                        } else {
                            stringResource(Res.string.feature_category_create)
                        }
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CategoryForm(
                state = state,
                onAction = viewModel::trySendAction,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScreenPadding)
            )

            if (state.showOverlay) {
                LoadingOverlay()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryForm(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FieldSpacing)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onAction(CategoryAction.NameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.feature_category_name)) },
            isError = state.nameError != null,
            supportingText = state.nameError?.let { { Text(it) } },
            singleLine = true
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { onAction(CategoryAction.DescriptionChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.feature_category_description)) },
            maxLines = 6
        )

        CategoryTypeSelector(
            selected = state.type,
            onSelected = { onAction(CategoryAction.TypeChanged(it)) }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = state.isSaveButtonEnabled,
            onClick = { onAction(CategoryAction.SaveCategoryClicked) }
        ) {
            Text(
                text = if (state.isEditMode) {
                    stringResource(Res.string.feature_category_edit_cat)
                } else {
                    stringResource(Res.string.feature_category_create)
                }
            )
        }
    }
}

/**
 * Fully custom dropdown. No ExposedDropdownMenu*, no DropdownMenu, no DropdownMenuItem.
 * Uses only Box / Surface / Column / Text / Icon / clickable.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoryTypeSelector(
    selected: CategoryType,
    onSelected: (CategoryType) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Category Type"
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Trigger field (styled like an outlined text field)
        Surface(
            onClick = { expanded = !expanded },
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selected.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "\u25BC", // ▼
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
        }

        // Menu
        if (expanded) {
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    CategoryType.entries.forEach { type ->
                        val isSelected = type == selected
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = false
                                    onSelected(type)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

private val CategoryType.displayName: String
    get() = name.replace('_', ' ')