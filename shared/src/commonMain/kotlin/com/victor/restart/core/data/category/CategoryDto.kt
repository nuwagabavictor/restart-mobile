package com.victor.restart.core.data.category

import com.victor.restart.core.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class CategoryDto(
    val id: Long,
    val categoryName: String,
    val categoryDescription: String? = null,
    val categoryType: String,
    val isActive: Boolean,

    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)