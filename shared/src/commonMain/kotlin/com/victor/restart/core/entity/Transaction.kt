package com.victor.restart.core.entity

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Long,
    val categoryName: String,
    val categoryId: Long,
    val categoryType: String,
    val transactionType: String,
    val amount: Double,
    val description: String?,
    val transactionDate: LocalDateTime,
    val createdAt: LocalDateTime

)
