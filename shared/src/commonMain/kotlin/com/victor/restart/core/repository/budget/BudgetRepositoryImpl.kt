package com.victor.restart.core.repository.budget

import com.victor.restart.core.data.budget.BudgetRequestDto
import com.victor.restart.core.data.budget.BudgetUpdateRequestDto
import com.victor.restart.core.entity.Budget
import com.victor.restart.core.mapper.BudgetMapper.toDomain
import com.victor.restart.core.network.DataManager
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BudgetRepositoryImpl(
    private val dataManager: DataManager,
    private val ioDispatcher: CoroutineDispatcher
): BudgetRepository {
    override suspend fun createBudget(requestDto: BudgetRequestDto): DataState<String> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.budgetApi.createBudget(requestDto);
                Logger.d("CreateBudget", "Response $response")
                DataState.Success(response)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun updateBudget(
        id: Long,
        request: BudgetUpdateRequestDto
    ): DataState<String> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.budgetApi.updateBudget(id,request);
                Logger.d("UpdateBudget", "Response $response")
                DataState.Success(response)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun findBudget(id: Long): DataState<Budget> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.budgetApi.findBudget(id);
                val budget = response.budget.toDomain()
                Logger.d("FindBudget", "Response $budget")
                DataState.Success(budget)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun findAllBudgets(): DataState<List<Budget>> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.budgetApi.findBudgets();
                val budget = response.budgets.toDomain()
                Logger.d("FindBudgets", "Response $budget")
                DataState.Success(budget)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }
}