package com.victor.restart.feature.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.data.budget.BudgetRequestDto
import com.victor.restart.core.data.budget.BudgetUpdateRequestDto
import com.victor.restart.core.entity.Budget
import com.victor.restart.core.entity.BudgetPeriod
import com.victor.restart.core.entity.Category
import com.victor.restart.core.repository.budget.BudgetRepository
import com.victor.restart.core.repository.category.CategoryRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import com.victor.restart.core.utils.ScreenUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_budget_period_required
import restart.shared.generated.resources.feature_transaction_amount_invalid
import restart.shared.generated.resources.feature_transaction_amount_positive
import restart.shared.generated.resources.feature_transaction_amount_required
import restart.shared.generated.resources.feature_transaction_category_required

class BudgetViewModel (
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
): BaseViewModel<BudgetState, BudgetEvent, BudgetAction>(
    initialState = BudgetState(uiState = ScreenUiState.Success)
){
    private var findBudgetJob: Job? = null
    private var findBudgetsJob: Job? = null
    private var saveBudgetJob: Job? = null
    private var findCategoriesJob: Job? = null

    init {
        val budgetId = savedStateHandle.get<Long>("budgetId")
        val categoryId = savedStateHandle.get<Long>("categoryId")

        trySendAction(BudgetAction.LoadCategories)

        if (budgetId != null && budgetId > 0L) {
            trySendAction(
                BudgetAction.LoadBudget(budgetId)
            )
        } else {
            updateState {
                it.copy(
                    categoryId = categoryId
                )
            }

            trySendAction(BudgetAction.LoadBudgets)
        }
    }

    private fun updateState(block: (BudgetState) -> BudgetState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: BudgetAction) {
        when(action){
            is BudgetAction.EditBudgetClicked -> {
                sendEvent(BudgetEvent.NavigateToEdit(action.budget.id))

            }
            is BudgetAction.LoadBudget -> {findBudget(action.id)}
            is BudgetAction.LoadBudgets -> {findBudgets()}
            is BudgetAction.SaveBudgetClicked -> {
                saveBudget()
            }
            is BudgetAction.AmountChanged -> {
                updateState {
                    it.copy(
                        amount = action.amount,
                        amountError = null,
                        isError = false
                    )
                }
            }
            is BudgetAction.PeriodChanged -> {
                updateState {
                    it.copy(
                        period = action.period,
                        periodError = null,
                        isError = false
                    )
                }
            }
            is BudgetAction.CategoryChanged -> {
                updateState {
                    it.copy(
                        categoryId = action.category.id,
                        categoryError = null,
                        isError = false
                    )
                }
            }
            is BudgetAction.Internal.ReceiveBudgetResult -> handleSaveResult(action.result)
            is BudgetAction.Internal.ReceiveBudgetsResult -> handleLoadBudgets(action.result)
            is BudgetAction.LoadCategories -> {
                findCategories()
            }
            is BudgetAction.SearchQueryChanged -> {
                updateState {
                    it.copy(
                        searchQuery = action.query
                    )
                }
            }
            is BudgetAction.Internal.LoadBudgetResult -> handleLoadBudgetResult(action.result)
            is BudgetAction.ErrorDialogDismiss -> {
                updateState {
                    it.copy(
                        dialogState = null
                    )
                }
            }
            is BudgetAction.Internal.ReceiveCategoriesResult -> handleLoadCategories(action.result)
        }
    }

    private fun handleLoadCategories(result: DataState<List<Category>>){
        when(result){
            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }
            is DataState.Success -> {
                val categories = result.data
                updateState {
                    it.copy(
                        categories = categories,
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
                        dialogState = BudgetState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }


    private fun handleLoadBudgets(result: DataState<List<Budget>>){
        when(result){
            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }
            is DataState.Success -> {
                val budgets = result.data
                updateState {
                    it.copy(
                        budgets = budgets,
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
                        dialogState = BudgetState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }
    private fun handleLoadBudgetResult(result: DataState<Budget>) {

        when (result) {

            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                val budget = result.data
                Logger.d("Budget:" ,"DATA = ${result.data}")



                updateState {
                    it.copy(
                        id = budget.id,
                        categoryId = budget.categoryId,
                        period = budget.period,
                        amount = budget.amount.toString(),
                        isEditMode = true,
                        showOverlay = false,
                        isError = false,
                        amountError = null,
                        categoryError = null
                    )
                }
            }

            is DataState.Error -> {
                println("Budget: ERROR = ${result.message}")

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = BudgetState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }
    private fun handleSaveResult(result: DataState<String>){

        when(result){
            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }
            is DataState.Success -> {
                val budget = result.data

                Logger.d("Budget Saved", "Result $budget")

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = false
                    )
                }

                findBudgets()

                sendEvent(BudgetEvent.ShowToast(result.data))

                sendEvent(BudgetEvent.NavigateBack)

            }
            is DataState.Error -> {
                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = BudgetState.DialogState.Error(result.message)
                    )
                }
            }
        }

    }

    private fun findBudget(id: Long){
        findBudgetJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findBudgetJob = viewModelScope.launch {
            val result = budgetRepository.findBudget(id)
            Logger.d("Budget", "Result $result")
            sendAction(BudgetAction.Internal.LoadBudgetResult(result))
        }
    }

    private fun findCategories(){
        findCategoriesJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findCategoriesJob = viewModelScope.launch {
            val result = categoryRepository.findCategories()
            Logger.d("Categories", "Result $result")
            sendAction(BudgetAction.Internal.ReceiveCategoriesResult(result))
        }
    }

    private fun findBudgets(){
        findBudgetsJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findBudgetsJob = viewModelScope.launch {
            val result = budgetRepository.findAllBudgets()
            Logger.d("Budgets", "Result $result")
            sendAction(BudgetAction.Internal.ReceiveBudgetsResult(result))
        }
    }

    private fun saveBudget(){
        if (!validate()) return

        saveBudgetJob?.cancel()

        updateState {
            it.copy(showOverlay = true)
        }

        val createRequest = BudgetRequestDto(
            amount = state.amount.trim().toDoubleOrNull() ?: 0.0,
            period = state.period.name,
            categoryId = requireNotNull(state.categoryId)
        )

        val updateRequest = BudgetUpdateRequestDto(
            amount = state.amount.trim().toDoubleOrNull() ?: 0.0,
            period = state.period.name,
        )

        saveBudgetJob = viewModelScope.launch {

            val result = if (state.isEditMode){
                budgetRepository.updateBudget(
                    id = requireNotNull(state.id),
                    request = updateRequest
                )
            }else{
                budgetRepository.createBudget(
                    requestDto = createRequest,
                )
            }

            sendAction(BudgetAction.Internal.ReceiveBudgetResult(result))
        }
    }

    private fun validate(): Boolean {

        val amount = state.amount.trim().toDoubleOrNull()

        val amountError = when {
            state.amount.isBlank() -> Res.string.feature_transaction_amount_required
            amount == null -> Res.string.feature_transaction_amount_invalid
            amount <= 0 -> Res.string.feature_transaction_amount_positive
            else -> null
        }

        val categoryError = when {
            state.categoryId == null -> Res.string.feature_transaction_category_required
            else -> null
        }

        val periodError = when {
            state.period.name.isEmpty() -> Res.string.feature_budget_period_required
            else -> null
        }

        updateState {
            it.copy(
                isError = amountError != null || categoryError != null,
                amountError = amountError,
                periodError = periodError,
                categoryError = categoryError
            )
        }

        return amountError == null && categoryError == null
    }
}


data class BudgetState(
    val id: Long? = null,

    val categoryId: Long? = null,

    val amount: String = "",

    val period: BudgetPeriod = BudgetPeriod.WEEKLY,

    val amountError: StringResource? = null,

    val periodError: StringResource? = null,

    val categoryError: StringResource? = null,

    val uiState: ScreenUiState,

    val isError : Boolean = false,

    val isEditMode: Boolean = false,

    val budgets: List<Budget> = emptyList(),

    val categories: List<Category> = emptyList(),

    val searchQuery: String = "",

    val dialogState: DialogState? = null,

    val showOverlay: Boolean = false,

    ){
    sealed interface DialogState{
        data class Error(val message: String): DialogState
    }

    val selectedCategory: Category?
        get() = categories.find {
            it.id == categoryId
        }

    val isSaveButtonEnabled: Boolean
        get() = categoryId != null && amount.trim().isNotEmpty() &&
                amount.toDoubleOrNull()?.let { it > 0 } == true

    val filteredBudgets: List<Budget>
        get() {

            val query = searchQuery.trim()

            if (query.isEmpty()) {
                return budgets
            }

            return budgets.filter { budget ->

                budget.categoryName.contains(query, ignoreCase = true) ||
                        budget.period.name.contains(query, ignoreCase = true)
            }
        }
}

sealed interface BudgetEvent{

    data object NavigateBack : BudgetEvent

    data class ShowToast(val message: String) : BudgetEvent

    data class NavigateToEdit(val id: Long) : BudgetEvent
}

sealed interface BudgetAction{
    data class CategoryChanged(val category: Category) : BudgetAction

    data class AmountChanged(val amount: String) : BudgetAction

    data class PeriodChanged(val period: BudgetPeriod) : BudgetAction

    data class SearchQueryChanged(val query: String) : BudgetAction

    data class EditBudgetClicked(val budget: Budget) : BudgetAction

    data object LoadBudgets : BudgetAction

    data object LoadCategories : BudgetAction

    data class LoadBudget(val id: Long) : BudgetAction

    data object SaveBudgetClicked : BudgetAction

    data object ErrorDialogDismiss : BudgetAction

    sealed class Internal : BudgetAction {

        data class ReceiveBudgetResult(val result: DataState<String>) : Internal()

        data class ReceiveBudgetsResult(val result: DataState<List<Budget>>) : Internal()

        data class LoadBudgetResult(val result: DataState<Budget>) : Internal()

        data class ReceiveCategoriesResult(val result: DataState<List<Category>>) : Internal()
    }
}
