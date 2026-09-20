package com.victor.restart.core.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int ,
    val username: String? = null,
    val email: String,
    val phone: String? = null,
    val role: String,
    val accessToken: String,
    val isAuthenticated: Boolean
)