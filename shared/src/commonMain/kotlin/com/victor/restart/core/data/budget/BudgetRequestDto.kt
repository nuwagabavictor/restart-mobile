package com.victor.restart.core.data.budget

import kotlinx.serialization.Serializable


@Serializable
data class BudgetRequestDto(
    val amount: Double,
    val period: String,
    val categoryId: Long
)

@Serializable
data class BudgetUpdateRequestDto(
    val amount: Double,
    val period: String,
)
