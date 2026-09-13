package com.victor.restart.core.mapper

import com.victor.restart.core.entity.User
import com.victor.restart.core.data.login.LoginRequestDto
import com.victor.restart.core.data.login.LoginResponseDto

object UserMapper {

    fun toDomain(dto: LoginResponseDto): User{
        return User(
            id = dto.user.id,
            email = dto.user.email,
            username = dto.user.username,
            role = dto.user.role,
            accessToken = dto.accessToken,
            isAuthenticated = dto.user.isAuthenticated
        )
    }
    fun toDto(email: String, password: String): LoginRequestDto {
        return LoginRequestDto(
            email = email,
            password = password
        )
    }

}

