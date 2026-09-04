package com.victor.restart.core.service

import com.victor.restart.core.ApiEndPoints
import com.victor.restart.core.data.login.LoginRequestDto
import com.victor.restart.core.data.login.LoginResponseDto
import com.victor.restart.core.data.register.PasswordRequest
import com.victor.restart.core.data.register.RegisterRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface UserService {

    @POST(ApiEndPoints.AUTHENTICATION)
    suspend fun login(@Body loginPayload: LoginRequestDto): LoginResponseDto;

    @POST(ApiEndPoints.REGISTER_USER)
    suspend fun createUser(@Body registerRequestDto: RegisterRequestDto): HttpResponse;

    @POST(ApiEndPoints.CHANGE_PASSWORD + "/{userId}")
    suspend fun changePassword(
        @Path("userId") userId: Int,
        @Body passwordRequest: PasswordRequest
    ): HttpResponse;

}