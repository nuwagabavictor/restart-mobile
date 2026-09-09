package com.victor.restart.core.data.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryUpdateRequestDto(
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val active: Boolean? = null
)