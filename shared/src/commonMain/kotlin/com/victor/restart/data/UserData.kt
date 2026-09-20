package com.victor.restart.data

data class UserData(
    val isAuthenticated: Boolean,
    val username: String,
    val password: String,
    val email: String,
    val phone: String,
    val role: String
)
