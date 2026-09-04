package com.victor.restart.core.repository

import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.utils.DataState
import com.victor.restart.data.AppSettings
import com.victor.restart.data.UserData
import com.victor.restart.di.Dispatcher
import com.victor.restart.di.RestartDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
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
    get() = preferencesHelper.userInfo.zip(preferencesHelper.settingsInfo) { account, settings ->
        when {
            account.isAuthenticated && settings.isAuthenticated &&
                    account.accessToken != null ->
                account.accessToken

            else -> null
        }
    }.map {
        if (it != null) AuthState.Authenticated(it) else AuthState.Unauthenticated
    }.stateIn(
        scope = unconfinedScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = AuthState.Unauthenticated,
    )

    override val settingsState: StateFlow<AppSettings>
    get() = preferencesHelper.settingsInfo
}