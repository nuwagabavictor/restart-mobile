package com.victor.restart.core.repository.user

import com.victor.restart.core.entity.RegisterPayload
import com.victor.restart.core.entity.User
import com.victor.restart.core.utils.DataState

interface UserRepository {
    suspend fun createUser(payload: RegisterPayload): DataState<String>;
    suspend fun login(email: String, password: String): DataState<User>;
    suspend fun changePassword(password: String, confirmPassword: String): DataState<String>;
}