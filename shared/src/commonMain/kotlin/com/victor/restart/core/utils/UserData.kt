package com.victor.restart.core.utils

import kotlinx.serialization.Serializable

@Serializable
data class UserData (
    val id: Int ,
    val email: String = "",
    val username: String = "",
    val role: String = "",
    val accessToken: String? = "",
    val isAuthenticated: Boolean = false,
    val password: String = "",

    ){
    companion object{
        val DEFAULT = UserData(
            id = -1,
            email = "",
            username = "",
            role = "",
            accessToken = "",
            isAuthenticated = false,
            password = ""
        )
    }
}
