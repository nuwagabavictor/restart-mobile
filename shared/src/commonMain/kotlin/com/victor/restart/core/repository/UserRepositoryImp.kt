package com.victor.restart.core.repository

import com.victor.restart.core.entity.RegisterPayload
import com.victor.restart.core.entity.User
import com.victor.restart.core.mapper.PasswordMapper
import com.victor.restart.core.mapper.RegisterMapper
import com.victor.restart.core.mapper.UserMapper
import com.victor.restart.core.network.DataManager
import com.victor.restart.core.utils.DataState
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRepositoryImp(
    private val dataManager: DataManager,
    private val ioDispatcher: CoroutineDispatcher
): UserRepository {

    override suspend fun createUser(payload: RegisterPayload): DataState<String> {
        return withContext(ioDispatcher) {
            try {
                val requestDto = RegisterMapper.toMap(payload);
                val response = dataManager.userApi.createUser(requestDto);
                DataState.Success(response.bodyAsText())

            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): DataState<User> {
        return withContext(ioDispatcher) {
            try {
                val payload = UserMapper.toDto(email, password);
                val response = dataManager.userApi.login(payload);
                val user = UserMapper.toDomain(response);
                DataState.Success(user);
            } catch (e: Exception) {
                DataState.Error( e)
            }
        }
    }

    override suspend fun changePassword(
        userId: Int,
        password: String,
        confirmPassword: String
    ): DataState<String> {
        return withContext(ioDispatcher) {
            try {
                val payload = PasswordMapper.toDto(password, confirmPassword);
                val response = dataManager.userApi.changePassword(userId,payload)
                DataState.Success(response.bodyAsText())
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }
}