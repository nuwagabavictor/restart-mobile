package com.victor.restart.core.mapper

import com.victor.restart.core.data.category.CategoryDto
import com.victor.restart.core.data.category.CategoryRequest
import com.victor.restart.core.entity.Category
import com.victor.restart.core.entity.CategoryType

object CategoryMapper {

    fun CategoryDto.toDomain(): Category{
        return Category(
            id = id,
            type = CategoryType.valueOf(categoryType),
            name = categoryName,
            description = categoryDescription,
            active = isActive,
            createdAt = createdAt
        )
    }

    fun toDto(name: String, type: String, description: String?): CategoryRequest{
        return CategoryRequest(
            name = name,
            description = description,
            type = type
        )
    }

    fun List<CategoryDto>.toDomain(): List<Category>{
        return map { it.toDomain() }
    }
}