package com.victor.restart.core.data.transaction

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponseDto(
    val transaction: TransactionDto
)

@Serializable
data class TransactionsResponseDto(
    val transactions: List<TransactionDto>
)
