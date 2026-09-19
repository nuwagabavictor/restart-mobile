package com.victor.restart.core.data.budget

@Serializable
data class BudgetsResponseDto(
    val budgets: List<BudgetDto>
)

@Serializable
data class BudgetResponseDto(
    val budget: BudgetDto
)
