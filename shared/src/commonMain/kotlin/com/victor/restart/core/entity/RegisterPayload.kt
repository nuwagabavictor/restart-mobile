package com.victor.restart.core.entity

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPayload(
    val username: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
)
