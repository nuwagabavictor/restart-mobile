package com.victor.restart.core.data.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val message: String,
    val refreshToken: String,
    val user: User
)