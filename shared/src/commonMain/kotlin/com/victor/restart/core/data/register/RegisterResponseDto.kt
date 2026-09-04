package com.victor.restart.core.data.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val message: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String
)