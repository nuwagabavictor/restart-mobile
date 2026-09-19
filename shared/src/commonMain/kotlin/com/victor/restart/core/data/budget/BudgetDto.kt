package com.victor.restart.core.data.budget

import com.victor.restart.core.utils.LocalDateTimeSerializer
import com.victor.restart.core.utils.QuotedDoubleSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    val id: Long,
    @Serializable(with = QuotedDoubleSerializer::class)
    val amount: Double,
    val period: String,
    @Serializable(with = QuotedDoubleSerializer::class)
    val amountSpent: Double,
    @Serializable(with = QuotedDoubleSerializer::class)
    val exceededAmount: Double,
    val categoryName: String,
    val categoryId: Long,
    val categoryType: String,
    @Serializable(LocalDateTimeSerializer::class)
    val startDate: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val endDate: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val isActive: Boolean,
    val isBudgetExceeded: Boolean


)
