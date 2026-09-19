package com.victor.restart.core.service

import com.victor.restart.core.ApiEndPoints
import com.victor.restart.core.data.budget.BudgetRequestDto
import com.victor.restart.core.data.budget.BudgetResponseDto
import com.victor.restart.core.data.budget.BudgetUpdateRequestDto
import com.victor.restart.core.data.budget.BudgetsResponseDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface BudgetService {

    @POST(ApiEndPoints.CREATE_BUDGET)
    suspend fun createBudget(@Body requestDto: BudgetRequestDto): String

    @PUT(ApiEndPoints.UPDATE_BUDGET + "{id}" + "/update")
    suspend fun updateBudget(@Path("id") id: Long, @Body requestDto: BudgetUpdateRequestDto): String

    @GET(ApiEndPoints.FIND_BUDGET + "{id}")
    suspend fun findBudget(@Path("id") id: Long): BudgetResponseDto

    @GET(ApiEndPoints.FIND_BUDGETS)
    suspend fun findBudgets(): BudgetsResponseDto
}