package com.victor.restart.core.repository

import com.victor.restart.core.data.category.CategoryRequest
import com.victor.restart.core.data.category.CategoryUpdateRequestDto
import com.victor.restart.core.entity.Category
import com.victor.restart.core.utils.DataState

interface CategoryRepository {

    suspend fun createCategory(request: CategoryRequest): DataState<String>

    suspend fun updateCategory(id: Long, requestDto: CategoryUpdateRequestDto): DataState<String>

    suspend fun findCategories(): DataState<List<Category>>

    suspend fun deleteCategory(id: Long): DataState<Unit>

    suspend fun getCategory(id: Long): DataState<Category>
}