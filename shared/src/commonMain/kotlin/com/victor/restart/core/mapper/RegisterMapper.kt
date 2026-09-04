package com.victor.restart.core.mapper

import com.victor.restart.core.data.register.RegisterRequestDto
import com.victor.restart.core.entity.RegisterPayload

object RegisterMapper {

    fun toMap(register: RegisterPayload): RegisterRequestDto{
        return RegisterRequestDto(
            username = register.username,
            email = register.email,
            role = register.role,
            password = register.password,
            phone = register.phone
        )
    }

}