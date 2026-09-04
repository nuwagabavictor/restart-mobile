package com.victor.restart.core.mapper

import com.victor.restart.core.data.register.PasswordRequest

object PasswordMapper {

    fun toDto(password: String, confirmPassword: String): PasswordRequest =
        PasswordRequest(
            password = password,
            confirmPassword = confirmPassword
        )
}