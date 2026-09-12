package com.victor.restart.core.data.transaction

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val categoryId: Long,
    val amount: Int,
    val description: String? = null,
)
