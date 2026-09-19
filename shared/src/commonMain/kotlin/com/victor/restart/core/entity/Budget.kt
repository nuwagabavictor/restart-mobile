package com.victor.restart.core.entity

import com.victor.restart.core.utils.LocalDateTimeSerializer
import com.victor.restart.core.utils.QuotedDoubleSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

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
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val isActive: Boolean,
    val isBudgetExceeded: Boolean
)
