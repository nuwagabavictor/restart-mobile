package com.victor.restart.core.service

import com.victor.restart.core.ApiEndPoints
import com.victor.restart.core.data.category.CategoryCreateResponseDto
import com.victor.restart.core.data.category.CategoryDto
import com.victor.restart.core.data.category.CategoryListResponseDto
import com.victor.restart.core.data.category.CategoryRequest
import com.victor.restart.core.data.category.CategoryResponseDto
import com.victor.restart.core.data.category.CategoryUpdateRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface CategoryService {

    @POST(ApiEndPoints.CREATE_CATEGORY)
    suspend fun createCategory(@Body request: CategoryRequest): CategoryCreateResponseDto;

    @PUT(ApiEndPoints.UPDATE_CATEGORY + "{id}" +"/update")
    suspend fun updateCategory(@Path("id") id: Long, @Body request: CategoryUpdateRequestDto): CategoryCreateResponseDto;

    @DELETE(ApiEndPoints.DELETE_CATEGORY + "{id}")
    suspend fun deleteCategory(@Path("id") id: Long)

    @GET(ApiEndPoints.FIND_CATEGORIES)
    suspend fun getCategories(): CategoryListResponseDto

    @GET(ApiEndPoints.FIND_CATEGORY + "{id}")
    suspend fun getCategory(@Path("id") id: Long): CategoryResponseDto
}