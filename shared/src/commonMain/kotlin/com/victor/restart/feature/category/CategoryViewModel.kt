package com.victor.restart.feature.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.data.category.CategoryRequest
import com.victor.restart.core.data.category.CategoryUpdateRequestDto
import com.victor.restart.core.entity.Category
import com.victor.restart.core.entity.CategoryType
import com.victor.restart.core.repository.category.CategoryRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<CategoryState, CategoryEvent, CategoryAction>(
    initialState = CategoryState(uiState = ScreenUiState.Success)
) {

    private var findCategoriesJob: Job? = null
    private var loadCategoryJob: Job? = null
    private var saveCategoryJob: Job? = null
    private var deleteCategoryJob: Job? = null

    init {
        println("CATEGORY VM: initialized")

        savedStateHandle.get<Long>("categoryId")?.let { categoryId ->
            println("CATEGORY VM: loading category id=$categoryId")

            trySendAction(
                CategoryAction.LoadCategory(categoryId)
            )
        } ?: run {
            println("CATEGORY VM: loading categories")

            trySendAction(
                CategoryAction.LoadCategories
            )
        }
    }

    private fun updateState(block: (CategoryState) -> CategoryState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: CategoryAction) {

        when (action) {

            is CategoryAction.NameChanged -> {
                updateState {
                    it.copy(
                        name = action.name,
                        nameError = null,
                        isError = false
                    )
                }
            }

            is CategoryAction.DescriptionChanged -> {
                updateState {
                    it.copy(
                        description = action.description,
                        isError = false
                    )
                }
            }

            is CategoryAction.TypeChanged -> {
                updateState {
                    it.copy(
                        type = action.type,
                        typeError = null,
                        isError = false
                    )
                }
            }

            is CategoryAction.SearchQueryChanged -> {
                updateState {
                    it.copy(
                        searchQuery = action.query
                    )
                }
            }

            is CategoryAction.EditCategoryClicked -> {
                sendEvent(
                    CategoryEvent.NavigateToEdit(
                        id = action.category.id
                    )
                )
            }
            is CategoryAction.CategoryClicked -> {
                sendEvent(
                    CategoryEvent.NavigateToCategory(
                        action.category
                    )
                )
            }

            is CategoryAction.DeleteCategoryClicked -> {
                updateState {
                    it.copy(
                        categoryToDelete = action.category,
                        showDeleteDialog = true
                    )
                }
            }

            CategoryAction.DeleteCategoryConfirmed -> {
                deleteCategory()
            }

            CategoryAction.DeleteDialogDismiss -> {
                updateState {
                    it.copy(
                        showDeleteDialog = false,
                        categoryToDelete = null
                    )
                }
            }

            CategoryAction.LoadCategories -> {
                findCategories()
            }

            is CategoryAction.LoadCategory -> {
                loadCategory(action.id)
            }

            CategoryAction.SaveCategoryClicked -> {
                saveCategory()
            }

            CategoryAction.ErrorDialogDismiss -> {
                updateState {
                    it.copy(
                        dialogState = null
                    )
                }
            }

            is CategoryAction.Internal.ReceiveCategoryResult -> {
                handleCategoryResult(action.result)
            }

            is CategoryAction.Internal.ReceiveCategoriesResult -> {
                handleCategoriesResult(action.result)
            }

            is CategoryAction.Internal.ReceiveDeleteResult -> {
                handleDeleteResult(action.result)
            }

            is CategoryAction.Internal.LoadCategoryResult -> {
                handleLoadCategoryResult(action.result)
            }
        }
    }

    // -------------------------------------------------------------------------
    // FIND CATEGORIES
    // -------------------------------------------------------------------------

    private fun findCategories() {

        findCategoriesJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findCategoriesJob = viewModelScope.launch {

            val result = categoryRepository.findCategories()
            println("CATEGORY VM: repository result = $result")

            sendAction(CategoryAction.Internal.ReceiveCategoriesResult(result))
        }
    }

    private fun handleCategoriesResult(result: DataState<List<Category>>) {

        when (result) {

            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {
                updateState {
                    it.copy(
                        categories = result.data,
                        showOverlay = false,
                        isError = false,
                        dialogState = null
                    )
                }
            }

            is DataState.Error -> {
                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = CategoryState.DialogState.Error(
                            result.message
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // GET CATEGORY
    // -------------------------------------------------------------------------

    private fun loadCategory(id: Long) {

        loadCategoryJob?.cancel()

        updateState {
            it.copy(
                id = id,
                isEditMode = true,
                showOverlay = true
            )
        }

        loadCategoryJob = viewModelScope.launch {

            val result = categoryRepository.getCategory(id)

            sendAction(CategoryAction.Internal.LoadCategoryResult(result))
        }
    }

    private fun handleLoadCategoryResult(result: DataState<Category>) {

        when (result) {

            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                val category = result.data
                println("CATEGORY: DATA = ${result.data}")



                updateState {
                    it.copy(
                        id = category.id,
                        name = category.name,
                        description = category.description.orEmpty(),
                        type = category.type,
                        selectedCategory = category,
                        isEditMode = true,
                        showOverlay = false,
                        isError = false,
                        nameError = null,
                        typeError = null
                    )
                }
            }

            is DataState.Error -> {
                println("CATEGORY: ERROR = ${result.message}")

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = CategoryState.DialogState.Error(
                            result.message
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // SAVE CATEGORY
    // -------------------------------------------------------------------------

    private fun saveCategory() {

        if (!validate()) {
            return
        }

        saveCategoryJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        saveCategoryJob = viewModelScope.launch {

            val result = if (state.isEditMode) {

                categoryRepository.updateCategory(
                    id = requireNotNull(state.id),
                    requestDto = CategoryUpdateRequestDto(
                        name = state.name.trim(),
                        description = state.description.trim().ifBlank { null },
                        type = state.type.name
                    )
                )

            } else {

                categoryRepository.createCategory(
                    CategoryRequest(
                        name = state.name.trim(),
                        description = state.description.trim().ifBlank { null },
                        type = state.type.name
                    )
                )
            }

            sendAction(CategoryAction.Internal.ReceiveCategoryResult(result))
        }
    }

    private fun handleCategoryResult(result: DataState<String>) {

        when (result) {

            is DataState.Loading -> {
                updateState { it.copy(showOverlay = true) }
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = false
                    )
                }

                findCategories()
                sendEvent(CategoryEvent.ShowToast(result.data))

                sendEvent(CategoryEvent.NavigateBack)
            }

            is DataState.Error -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = CategoryState.DialogState.Error(
                            result.message
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // DELETE CATEGORY
    // -------------------------------------------------------------------------

    private fun deleteCategory() {

        val category = state.categoryToDelete ?: return

        deleteCategoryJob?.cancel()

        updateState {
            it.copy(
                showDeleteDialog = false,
                showOverlay = true
            )
        }

        deleteCategoryJob = viewModelScope.launch {

            val result = categoryRepository.deleteCategory(
                category.id
            )

            sendAction(CategoryAction.Internal.ReceiveDeleteResult(result))
        }
    }

    private fun handleDeleteResult(result: DataState<Unit>) {

        when (result) {

            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = false,
                        categoryToDelete = null
                    )
                }

                sendEvent(CategoryEvent.ShowToast("Category deleted successfully"))

                findCategories()
            }

            is DataState.Error -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = CategoryState.DialogState.Error(
                            result.message
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    private fun validate(): Boolean {

        val nameError =
            if (state.name.trim().isEmpty()) {
                "Category name is required"
            } else {
                null
            }

        val typeError =
            if (state.type.name.isEmpty()) {
                "Category type is required"
            } else {
                null
            }

        val hasError =
            nameError != null || typeError != null

        updateState {
            it.copy(
                isError = hasError,
                nameError = nameError,
                typeError = typeError
            )
        }

        return !hasError
    }
}

data class CategoryState(

    val id: Long? = null,

    val name: String = "",
    val description: String = "",
    val type: CategoryType = CategoryType.EXPENSE,

    val isEditMode: Boolean = false,

    val isError: Boolean = false,
    val nameError: String? = null,
    val typeError: String? = null,

    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",

    val categoryToDelete: Category? = null,
    val showDeleteDialog: Boolean = false,
    val selectedCategory: Category? = null,


    val uiState: ScreenUiState,

    val showOverlay: Boolean = false,

    val dialogState: DialogState? = null

) {

    sealed interface DialogState {

        data class Error(val message: String) : DialogState
    }

    val isSaveButtonEnabled: Boolean
        get() =
            name.trim().isNotEmpty() && type.name.isNotEmpty()

    val filteredCategories: List<Category>
        get() {

            val query = searchQuery.trim()

            if (query.isEmpty()) {
                return categories
            }

            return categories.filter { category ->

                category.name.contains(query, ignoreCase = true) ||
                        category.type.name.contains(query, ignoreCase = true)
            }
        }
}
sealed interface CategoryAction {

    data class NameChanged(val name: String) : CategoryAction

    data class DescriptionChanged(val description: String) : CategoryAction

    data class TypeChanged(val type: CategoryType) : CategoryAction

    data class SearchQueryChanged(val query: String) : CategoryAction

    data class EditCategoryClicked(val category: Category) : CategoryAction

    data class DeleteCategoryClicked(val category: Category) : CategoryAction

    data class CategoryClicked(val category: Long) : CategoryAction

    data object DeleteCategoryConfirmed : CategoryAction

    data object DeleteDialogDismiss : CategoryAction

    data object LoadCategories : CategoryAction

    data class LoadCategory(val id: Long) : CategoryAction

    data object SaveCategoryClicked : CategoryAction

    data object ErrorDialogDismiss : CategoryAction

    sealed class Internal : CategoryAction {

        data class ReceiveCategoryResult(val result: DataState<String>) : Internal()

        data class ReceiveCategoriesResult(val result: DataState<List<Category>>) : Internal()

        data class ReceiveDeleteResult(val result: DataState<Unit>) : Internal()

        data class LoadCategoryResult(val result: DataState<Category>) : Internal()
    }
}

sealed interface CategoryEvent {

    data object NavigateBack : CategoryEvent

    data class ShowToast(val message: String) : CategoryEvent

    data class NavigateToEdit(val id: Long) : CategoryEvent

    data class NavigateToCategory(val categoryId: Long) : CategoryEvent

}