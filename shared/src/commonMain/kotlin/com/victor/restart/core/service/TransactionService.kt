package com.victor.restart.core.service

import com.victor.restart.core.ApiEndPoints
import com.victor.restart.core.data.transaction.TransactionRequest
import com.victor.restart.core.data.transaction.TransactionResponseDto
import com.victor.restart.core.data.transaction.TransactionsResponseDto
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface TransactionService{

    @POST(ApiEndPoints.CREATE_TRANSACTION)
    suspend fun createTransaction(@Body request: TransactionRequest): String

    @PUT(ApiEndPoints.UPDATE_TRANSACTION + "{id}" + "/update")
    suspend fun updateTransaction(@Path("id") id: Long, @Body request: TransactionRequest): String

    @GET(ApiEndPoints.FIND_TRANSACTIONS)
    suspend fun findTransactions(): TransactionsResponseDto

    @GET(ApiEndPoints.FIND_TRANSACTION + "{id}")
    suspend fun findTransaction(@Path("id") id: Long): TransactionResponseDto
}