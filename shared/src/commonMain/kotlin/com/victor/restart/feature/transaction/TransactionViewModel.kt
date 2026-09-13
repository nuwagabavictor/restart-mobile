package com.victor.restart.feature.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.data.transaction.TransactionRequest
import com.victor.restart.core.entity.Category
import com.victor.restart.core.entity.Transaction
import com.victor.restart.core.repository.category.CategoryRepository
import com.victor.restart.core.repository.transaction.TransactionRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import com.victor.restart.core.utils.ScreenUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_transaction_amount_invalid
import restart.shared.generated.resources.feature_transaction_amount_positive
import restart.shared.generated.resources.feature_transaction_amount_required
import restart.shared.generated.resources.feature_transaction_category_required

class TransactionViewModel (
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
): BaseViewModel<TransactionState, TransactionEvent, TransactionAction>(
    initialState = TransactionState(uiState = ScreenUiState.Success)
){
    private var findTransactionJob: Job? = null
    private var findTransactionsJob: Job? = null
    private var saveTransactionJob: Job? = null
    private var findCategoriesJob: Job? = null

    init {
        val id = savedStateHandle.get<Long>("transactionId")

        trySendAction(TransactionAction.LoadCategories)   // ← both modes need this

        if (id != null && id > 0L) {
            trySendAction(TransactionAction.LoadTransaction(id))
        } else {
            trySendAction(TransactionAction.LoadTransactions)
        }
    }

    override fun handleAction(action: TransactionAction) {
        when(action){
            is TransactionAction.LoadTransaction -> findTransaction(action.id)
            is TransactionAction.LoadTransactions -> findTransactions()
            is TransactionAction.LoadCategories -> findCategories()
            is TransactionAction.AmountChanged -> {
                updateState {
                    it.copy(
                        amount = action.amount,
                        amountError = null,
                        isError = false
                    )
                }
            }
            is TransactionAction.EditTransactionClicked -> {
                sendEvent(TransactionEvent.NavigateToEdit(action.transaction.id))
            }
            is TransactionAction.ErrorDialogDismiss -> {
                updateState {
                    it.copy(
                        dialogState = null
                    )
                }
            }
            is TransactionAction.SaveTransactionClicked -> {
                saveTransaction()
            }
            is TransactionAction.CategoryChanged -> {
                updateState {
                    it.copy(
                        categoryId = action.category.id,
                        categoryError = null,
                        isError = false
                    )
                }
            }
            is TransactionAction.DescriptionChanged -> {
                updateState {
                    it.copy(
                        description = action.description,
                        isError = false
                    )
                }
            }
            is TransactionAction.Internal.LoadTransactionResult -> handleLoadTransactionResult(action.result)
            is TransactionAction.Internal.ReceiveTransactionResult -> handleSaveResult(action.result)
            is TransactionAction.Internal.ReceiveTransactionsResult -> handleLoadTransactions(action.result)
            is TransactionAction.Internal.ReceiveCategoriesResult -> handleLoadCategories(action.result)
            is TransactionAction.SearchQueryChanged -> {
                updateState {
                    it.copy(
                        searchQuery = action.query
                    )
                }
            }
        }
    }

    private fun updateState(block: (TransactionState) -> TransactionState) {
        mutableStateFlow.update(block)
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
                        dialogState = TransactionState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }


    private fun handleLoadTransactions(result: DataState<List<Transaction>>){
        when(result){
            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }
            is DataState.Success -> {
                val transactions = result.data
                updateState {
                    it.copy(
                        transactions = transactions,
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
                        dialogState = TransactionState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }
    private fun handleLoadTransactionResult(result: DataState<Transaction>) {

        when (result) {

            is DataState.Loading -> {
                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                val transaction = result.data
                Logger.d("Transaction:" ,"DATA = ${result.data}")



                updateState {
                    it.copy(
                        id = transaction.id,
                        categoryId = transaction.categoryId,
                        description = transaction.description.orEmpty(),
                        amount = transaction.amount.toString(),
                        isEditMode = true,
                        showOverlay = false,
                        isError = false,
                        amountError = null,
                        categoryError = null
                    )
                }
            }

            is DataState.Error -> {
                println("Transaction: ERROR = ${result.message}")

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = TransactionState.DialogState.Error(result.message)
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
                val transaction = result.data

                Logger.d("Transaction Saved", "Result $transaction")

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = false
                    )
                }

                findTransactions()

                sendEvent(TransactionEvent.ShowToast(result.data))

                sendEvent(TransactionEvent.NavigateBack)

            }
            is DataState.Error -> {
                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = TransactionState.DialogState.Error(result.message)
                    )
                }
            }
        }

    }
    private fun findTransactions(){
        findTransactionsJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findTransactionsJob = viewModelScope.launch {
            val result = transactionRepository.findTransactions()
            Logger.d("Transactions", "Result $result")
            sendAction(TransactionAction.Internal.ReceiveTransactionsResult(result))
        }
    }

    private fun findTransaction(id: Long){
        findTransactionJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        findTransactionJob = viewModelScope.launch {
            val result = transactionRepository.findTransaction(id)
            Logger.d("Transaction", "Result $result")
            sendAction(TransactionAction.Internal.LoadTransactionResult(result))
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
            sendAction(TransactionAction.Internal.ReceiveCategoriesResult(result))
        }
    }

    private fun saveTransaction() {

        if (!validate()) return

        saveTransactionJob?.cancel()

        updateState {
            it.copy(showOverlay = true)
        }

        val request = TransactionRequest(
            categoryId = requireNotNull(state.categoryId),
            amount = state.amount.trim().toDoubleOrNull() ?: 0.0,
            description = state.description.trim().ifBlank { null }
        )

        saveTransactionJob = viewModelScope.launch {

            val result = if (state.isEditMode) {
                transactionRepository.updateTransaction(
                    id = requireNotNull(state.id),
                    request = request
                )
            } else {
                transactionRepository.createTransaction(
                    request = request
                )
            }

            sendAction(
                TransactionAction.Internal.ReceiveTransactionResult(result)
            )
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

        updateState {
            it.copy(
                isError = amountError != null || categoryError != null,
                amountError = amountError,
                categoryError = categoryError
            )
        }

        return amountError == null && categoryError == null
    }
}

data class TransactionState(

    val id: Long? = null,

    val categoryId: Long? = null,

    val amount: String = "",
    val description: String = "",

    val isEditMode: Boolean = false,

    val isError: Boolean = false,
    val categoryError: StringResource? = null,
    val amountError: StringResource? = null,

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