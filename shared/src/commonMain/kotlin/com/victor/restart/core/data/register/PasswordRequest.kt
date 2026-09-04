package com.victor.restart.core.data.register

import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    val password: String? = null,
    val confirmPassword: String? = null
)
