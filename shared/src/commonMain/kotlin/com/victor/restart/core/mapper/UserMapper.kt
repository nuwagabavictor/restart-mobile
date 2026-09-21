package com.victor.restart.core.mapper

import com.victor.restart.core.entity.User
import com.victor.restart.core.data.login.LoginRequestDto
import com.victor.restart.core.data.login.LoginResponseDto

object UserMapper {

    fun toDomain(dto: LoginResponseDto): User{
        return User(
            id = dto.user.id,
            username = dto.user.username,
            email = dto.user.email,
            phone = dto.user.phone,
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

