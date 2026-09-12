package com.victor.restart.core.repository.transaction

import com.victor.restart.core.data.transaction.TransactionRequest
import com.victor.restart.core.entity.Transaction
import com.victor.restart.core.utils.DataState

interface TransactionRepository {
    suspend fun createTransaction(request: TransactionRequest): DataState<String>

    suspend fun updateTransaction(id: Long, request: TransactionRequest): DataState<String>

    suspend fun findTransactions(): DataState<List<Transaction>>

    suspend fun findTransaction(id: Long): DataState<Transaction>
}