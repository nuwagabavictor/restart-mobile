package com.victor.restart.feature.transaction

import androidx.lifecycle.SavedStateHandle
import com.victor.restart.core.entity.Category
import com.victor.restart.core.entity.Transaction
import com.victor.restart.core.repository.transaction.TransactionRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import com.victor.restart.feature.category.CategoryState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update

class TransactionViewModel (
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
): BaseViewModel<TransactionState, TransactionEvent, TransactionAction>(
    initialState = TransactionState(uiState = ScreenUiState.Success)
){
    private var findTransactionJob: Job? = null
    private var findTransactionsJob: Job? = null
    private var saveTransactionJob: Job? = null

    private fun updateState(block: (TransactionState) -> TransactionState) {
        mutableStateFlow.update(block)
    }

    private fun validate(): Boolean {

        val amountError =
            if (state.amount.trim().isEmpty()) {
                "Amount is required"
            } else if (state.amount.toDoubleOrNull() == null) {
                "Enter a valid amount"
            } else if (state.amount.toDouble() <= 0) {
                "Amount must be greater than 0"
            } else {
                null
            }

        val categoryError =
            if (state.categoryId == null) {
                "Category is required"
            } else {
                null
            }

        val hasError = amountError != null || categoryError != null

        if (hasError) {
            updateState {
                it.copy(
                    isError = true,
                    amountError = amountError,
                    categoryError = categoryError
                )
            }
        }

        return !hasError
    }
}

data class TransactionState(

    val id: Long? = null,

    val categoryId: Long? = null,

    val amount: String = "",
    val description: String = "",

    val isEditMode: Boolean = false,

    val isError: Boolean = false,
    val categoryError: String? = null,
    val amountError: String? = null,

    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),

    val searchQuery: String = "",

    val uiState: ScreenUiState,

    val showOverlay: Boolean = false,

    val dialogState: DialogState? = null

) {

    sealed interface DialogState {

        data class Error(val message: String) : DialogState
    }

    val selectedCategory: Category?
        get() = categories.find {
            it.id == categoryId
        }

    val isSaveButtonEnabled: Boolean
        get() = categoryId != null && amount.trim().isNotEmpty() &&
                    amount.toDoubleOrNull()?.let { it > 0 } == true

    val filteredTransactions: List<Transaction>
        get() {

            val query = searchQuery.trim()

            if (query.isEmpty()) {
                return transactions
            }

            return transactions.filter { transaction ->

                transaction.categoryName.contains(query, ignoreCase = true) ||
                        transaction.transactionType.contains(query, ignoreCase = true)
            }
        }
}

sealed interface TransactionEvent {

    data object NavigateBack : TransactionEvent

    data class ShowToast(val message: String) : TransactionEvent

    data class NavigateToEdit(val id: Long) : TransactionEvent
}

sealed interface TransactionAction {

    data class CategoryChanged(val category: Category) : TransactionAction

    data class AmountChanged(val amount: String) : TransactionAction

    data class DescriptionChanged(val description: String) : TransactionAction

    data class SearchQueryChanged(val query: String) : TransactionAction

    data class EditTransactionClicked(val transaction: Transaction) : TransactionAction

    data object LoadTransactions : TransactionAction

    data object LoadCategories : TransactionAction

    data class LoadTransaction(val id: Long) : TransactionAction

    data object SaveTransactionClicked : TransactionAction

    data object ErrorDialogDismiss : TransactionAction

    sealed class Internal : TransactionAction {

        data class ReceiveTransactionResult(val result: DataState<String>) : Internal()

        data class ReceiveTransactionsResult(val result: DataState<List<Transaction>>) : Internal()

        data class LoadTransactionResult(val result: DataState<Transaction>) : Internal()

        data class ReceiveCategoriesResult(val result: DataState<List<Category>>) : Internal()
    }
}