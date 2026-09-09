package com.victor.restart.feature.category

import androidx.lifecycle.SavedStateHandle
import com.victor.restart.core.repository.CategoryRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.ScreenUiState

@Suppress("TooManyFunctions")
class CategoryViewModel (
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
): BaseViewModel<CategoryState, CategoryEvent, CategoryAction>(
    initialState = CategoryState(uiState = ScreenUiState.Success)
){}

data class CategoryState(
    val uiState: ScreenUiState
)

sealed interface CategoryEvent{}

sealed interface CategoryAction{}