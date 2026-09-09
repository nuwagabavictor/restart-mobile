package com.victor.restart.core.data.category

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Long,
    val categoryName: String,
    val categoryDescription: String? = null,
    val categoryType: String,
    val isActive: Boolean,
    val createdAt: LocalDateTime
)