package com.victor.restart.core.repository.userdata

import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.utils.DataState
import com.victor.restart.data.AppSettings
import com.victor.restart.data.UserData
import com.victor.restart.di.Dispatcher
import com.victor.restart.di.RestartDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class UserDataRepositoryImpl  (
    private val preferencesHelper: UserPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(RestartDispatchers.Unconfined)
    private val unconfinedDispatcher: CoroutineDispatcher,
) : UserDataRepository {

    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)



    override val userData: Flow<DataState<UserData>> = flow {
        try {
            val userData = UserData(
                isAuthenticated = !preferencesHelper.token.value.isNullOrEmpty(),
                username = preferencesHelper.userInfo.firstOrNull()?.username ?: "",
                password = preferencesHelper.userInfo.firstOrNull()?.password ?: "",
                email = preferencesHelper.userInfo.firstOrNull()?.email ?: "",
                phone = preferencesHelper.userInfo.firstOrNull()?.phone ?: "",
                role = preferencesHelper.userInfo.firstOrNull()?.role ?: "",
                )
            emit(DataState.Success(userData))
        } catch (e: Exception) {
            emit(DataState.Error(e, null))
        }
    }.flowOn(ioDispatcher)



    override suspend fun logOut(): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                preferencesHelper.logOut()
            }
            DataState.Success("User logged out Successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override val authState: StateFlow<AuthState>
        get() = preferencesHelper.userInfo
            .map { user ->

                if (user.isAuthenticated && !user.accessToken.isNullOrBlank()) {
                    AuthState.Authenticated(user.accessToken)
                } else {
                    AuthState.Unauthenticated
                }
            }.stateIn(
        scope = unconfinedScope,
        started = SharingStarted.Eagerly,
        initialValue = AuthState.Loading,
    )

    override val settingsState: StateFlow<AppSettings>
    get() = preferencesHelper.settingsInfo
}