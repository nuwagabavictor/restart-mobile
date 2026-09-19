package com.victor.restart.core.entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

enum class BudgetPeriod{
    WEEKLY,
    MONTHLY,
    YEARLY
}

data class Budget(
    val id: Long,
    val amount: Double,
    val period: BudgetPeriod,
    val amountSpent: Double,
    val exceededAmount: Double,
    val categoryName: String,
    val categoryId: Long,
    val categoryType: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDateTime,
    val isActive: Boolean,
    val isBudgetExceeded: Boolean
)
