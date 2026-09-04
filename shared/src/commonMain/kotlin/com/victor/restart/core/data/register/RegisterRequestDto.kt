package com.victor.restart.core.data.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
)
