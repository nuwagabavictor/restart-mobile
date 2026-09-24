package com.victor.restart.feature.home



import androidx.lifecycle.viewModelScope
import com.victor.restart.core.entity.Transaction
import com.victor.restart.core.repository.notification.NotificationRepository
import com.victor.restart.core.repository.transaction.TransactionRepository
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferencesRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = HomeState(isLoading = true)
) {

    init {
        observeUser()
        load()
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.Load -> load()
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            userPreferences.userInfo.collect { user ->
                update { it.copy(userName = user.username) }
            }
        }
    }


    private fun load() {
        viewModelScope.launch {
            when (val result = transactionRepository.findTransactions()) {
                is DataState.Loading -> update { it.copy(isLoading = true) }

                is DataState.Success -> {
                    val txns = result.data
                    val income = txns
                        .filter { it.transactionType.equals("INCOME", true) }
                        .sumOf { it.amount }
                    val expense = txns
                        .filter { it.transactionType.equals("EXPENSE", true) }
                        .sumOf { it.amount }

                    update {
                        it.copy(
                            isLoading = false,
                            recentTransactions = txns.take(5),
                            totalIncome = income.toString(),
                            totalExpense = expense.toString(),
                            balance = (income - expense).toString()
                        )
                    }
                }

                is DataState.Error -> update { it.copy(isLoading = false) }
            }
        }
    }

    private fun update(block: (HomeState) -> HomeState) =
        mutableStateFlow.update(block)
}

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String? = null,
    val balance: String = "0",
    val totalIncome: String = "0",
    val totalExpense: String = "0",
    val recentTransactions: List<Transaction> = emptyList()
)

sealed interface HomeAction {
    data object Load : HomeAction
}

sealed interface HomeEvent