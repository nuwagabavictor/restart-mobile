package com.victor.restart.core.repository.transaction

import com.victor.restart.core.data.transaction.TransactionRequest
import com.victor.restart.core.entity.Transaction
import com.victor.restart.core.mapper.TransactionMapper.toDomain
import com.victor.restart.core.network.DataManager
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TransactionRepositoryImpl (
    private val dataManager: DataManager,
    private val ioDispatcher: CoroutineDispatcher
): TransactionRepository {

    override suspend fun createTransaction(request: TransactionRequest): DataState<String> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.transactionApi.createTransaction(request)
                Logger.d("TransCreation", "Response $response")
                DataState.Success(response)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun updateTransaction(
        id: Long,
        request: TransactionRequest
    ): DataState<String> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.transactionApi.updateTransaction(id,request)
                Logger.d("TransUpdate", "Response $response")
                DataState.Success(response)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun findTransactions(): DataState<List<Transaction>> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.transactionApi.findTransactions()
                val transactions = response.transactions.toDomain()
                Logger.d("TransCreation", "Response $transactions")
                DataState.Success(transactions)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }

    override suspend fun findTransaction(id: Long): DataState<Transaction> {
        return withContext(ioDispatcher){
            try {
                val response = dataManager.transactionApi.findTransaction(id)
                val transaction = response.transaction.toDomain()
                Logger.d("TransCreation", "Response $transaction")
                DataState.Success(transaction)
            }catch (e: Exception){
                DataState.Error(e)
            }
        }
    }
}