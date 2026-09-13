package com.victor.restart.core.data.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponseDto(
    val categories: List<CategoryDto>
)

@Serializable
data class CategoryResponseDto(
    val category: CategoryDto
)
