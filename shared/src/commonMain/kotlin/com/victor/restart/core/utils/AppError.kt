package com.victor.restart.core.utils

import kotlinx.serialization.Serializable


@Serializable
data class AppError(
    val errorMessage: String? = null,
    val httpStatusCode: String? = null,
    val message: String? = null,
)