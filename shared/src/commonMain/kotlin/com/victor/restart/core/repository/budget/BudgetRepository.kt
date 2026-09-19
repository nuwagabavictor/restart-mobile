package com.victor.restart.core.repository.budget

import com.victor.restart.core.data.budget.BudgetRequestDto
import com.victor.restart.core.data.budget.BudgetUpdateRequestDto
import com.victor.restart.core.entity.Budget
import com.victor.restart.core.utils.DataState

interface BudgetRepository {
    suspend fun createBudget(requestDto: BudgetRequestDto): DataState<String>
    suspend fun updateBudget(id: Long, request: BudgetUpdateRequestDto): DataState<String>
    suspend fun findBudget(id: Long): DataState<Budget>
    suspend fun findAllBudgets(): DataState<List<Budget>>
}