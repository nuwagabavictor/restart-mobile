package com.victor.restart.core.mapper

import com.victor.restart.core.data.budget.BudgetDto
import com.victor.restart.core.entity.Budget
import com.victor.restart.core.entity.BudgetPeriod

object BudgetMapper {

    fun BudgetDto.toDomain(): Budget{
        return Budget(
            id = id,
            amount = amount,
            period = BudgetPeriod.valueOf(period),
            amountSpent = amountSpent,
            exceededAmount = exceededAmount,
            categoryName = categoryName,
            categoryId = categoryId,
            categoryType = categoryType,
            startDate = startDate,
            endDate = endDate,
            createdAt = createdAt,
            isActive = isActive,
            isBudgetExceeded = isBudgetExceeded
        )
    }

    fun List<BudgetDto>.toDomain(): List<Budget>{
        return map { it.toDomain() }
    }
}