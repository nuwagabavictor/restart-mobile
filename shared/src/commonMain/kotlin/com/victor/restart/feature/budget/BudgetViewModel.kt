package com.victor.restart.feature.budget

import com.victor.restart.core.entity.Budget
import com.victor.restart.core.entity.BudgetPeriod
import com.victor.restart.core.entity.Category
import com.victor.restart.core.utils.ScreenUiState
import org.jetbrains.compose.resources.StringResource


data class BudgetState(
    val id: Long? = null,

    val categoryId: Long? = null,

    val amount: String = "",

    val period: BudgetPeriod = BudgetPeriod.WEEKLY,

    val amountError: StringResource? = null,

    val periodError: StringResource? = null,

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
}