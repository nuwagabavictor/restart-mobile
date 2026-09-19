package com.victor.restart.core.data.budget

import kotlinx.serialization.Serializable

@Serializable
data class BudgetsResponseDto(
    val budgets: List<BudgetDto>
)

@Serializable
data class BudgetResponseDto(
    val budget: BudgetDto
)
