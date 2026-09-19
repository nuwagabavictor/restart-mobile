package com.victor.restart.core.data.transaction

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val categoryId: Long,
    val amount: Double,
    val description: String? = null,
)
