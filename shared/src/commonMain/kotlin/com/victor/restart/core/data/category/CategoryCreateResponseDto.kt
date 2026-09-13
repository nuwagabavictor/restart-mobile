package com.victor.restart.core.data.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryCreateResponseDto(
    val message: String,
    val id: Long
)