package com.victor.restart.core.data.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponseDto(
    val data: List<CategoryDto>
)
