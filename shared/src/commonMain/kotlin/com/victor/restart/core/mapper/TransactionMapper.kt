package com.victor.restart.core.mapper

import com.victor.restart.core.data.transaction.TransactionDto
import com.victor.restart.core.entity.Transaction

object TransactionMapper {

    fun TransactionDto.toDomain(): Transaction{
        return Transaction(
            id = id,
            categoryName = categoryName,
            categoryId = categoryId,
            categoryType = categoryType,
            transactionType = transactionType,
            amount = amount,
            description = description,
            transactionDate = transactionDate,
            createdAt = createdAt
        )
    }

    fun List<TransactionDto>.toDomain(): List<Transaction>{
        return map { it.toDomain() }
    }
}