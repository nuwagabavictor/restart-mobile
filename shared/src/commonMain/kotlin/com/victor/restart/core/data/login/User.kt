package com.victor.restart.core.data.login

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val id: Int,
    val isAuthenticated: Boolean,
    val role: String,
    val username: String,
    val phone: String
)