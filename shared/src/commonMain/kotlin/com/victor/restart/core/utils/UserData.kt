package com.victor.restart.core.utils

import kotlinx.serialization.Serializable

@Serializable
data class UserData (
    val id: Int ,
    val email: String = "",
    val username: String = "",
    val phone: String = "",
    val role: String = "",
    val accessToken: String? = "",
    val refreshToken: String? = "",
    val isAuthenticated: Boolean = false,
    val password: String = "",

    ){
    companion object{
        val DEFAULT = UserData(
            id = -1,
            email = "",
            username = "",
            phone = "",
            role = "",
            accessToken = "",
            refreshToken = "",
            isAuthenticated = false,
            password = ""
        )
    }
}
