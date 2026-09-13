package com.victor.restart.core.entity

import kotlinx.datetime.LocalDateTime

enum class CategoryType{
    INCOME,EXPENSE
}

data class Category(
    val id: Long,
    val type: CategoryType,
    val name: String,
    val description: String?,
    val active: Boolean,
    val createdAt: LocalDateTime
)
