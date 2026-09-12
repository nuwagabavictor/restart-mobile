package com.victor.restart.core.repository.userdata

import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.utils.DataState
import com.victor.restart.data.AppSettings
import com.victor.restart.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserDataRepository {


    // TODO
    /**
     * Stream of [com.victor.restart.core.utils.UserData]
     */
    val userData: Flow<DataState<UserData>>

    val authState: StateFlow<AuthState>

    val settingsState: StateFlow<AppSettings>

    suspend fun logOut(): DataState<String>
}