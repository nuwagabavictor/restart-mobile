package com.victor.restart.core.data.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val name: String,
    val description: String? = null,
    val type: String
)
