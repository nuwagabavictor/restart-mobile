package com.victor.restart.core.repository.category

import com.victor.restart.core.data.category.CategoryRequest
import com.victor.restart.core.data.category.CategoryUpdateRequestDto
import com.victor.restart.core.entity.Category
import com.victor.restart.core.mapper.CategoryMapper.toDomain
import com.victor.restart.core.network.DataManager
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val dataManager: DataManager,
    private val ioDispatcher: CoroutineDispatcher
): CategoryRepository {

    override suspend fun createCategory(request: CategoryRequest): DataState<String> {
        return withContext(ioDispatcher) {
            try {
                val response = dataManager.categoryApi.createCategory(request);
                Logger.d("CategoryRepository", "Response: $response")
                DataState.Success(response.message)
            } catch (e: Exception) {
                Logger.e("CategoryRepository", "Create category failed", e)
                DataState.Error(e)
            }
        }
    }

    override suspend fun updateCategory(
        id: Long,
        requestDto: CategoryUpdateRequestDto
    ): DataState<String> {
        return withContext(ioDispatcher) {
            try {
                val response = dataManager.categoryApi.updateCategory(id, requestDto);
                DataState.Success(response.message)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun findCategories(): DataState<List<Category>> {
        return withContext(ioDispatcher) {
            try {
                val response = dataManager.categoryApi.getCategories();
                val categories = response.categories.toDomain()

                Logger.d("Category data", "Result $categories")
                DataState.Success(categories)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun deleteCategory(id: Long): DataState<Unit> {
        return withContext(ioDispatcher) {
            try {
                val response = dataManager.categoryApi.deleteCategory(id);
                DataState.Success(response)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun getCategory(id: Long): DataState<Category> {
        return withContext(ioDispatcher) {
            try {
                val response = dataManager.categoryApi.getCategory(id);
                Logger.d("Category", "Response: $response")
                DataState.Success(response.category.toDomain())
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }
}