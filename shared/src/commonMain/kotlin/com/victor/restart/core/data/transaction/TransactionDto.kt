package com.victor.restart.core.data.transaction

import com.victor.restart.core.utils.LocalDateTimeSerializer
import com.victor.restart.core.utils.QuotedDoubleSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: Long,
    val categoryName: String,
    val categoryId: Long,
    val categoryType: String,
    val transactionType: String,
    @Serializable(with = QuotedDoubleSerializer::class)
    val amount: Double,
    val description: String?,
    @Serializable(LocalDateTimeSerializer::class)
    val transactionDate: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)
