package com.victor.restart.core.repository

import com.victor.restart.core.enums.AuthState
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.UserData
import com.victor.restart.data.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserDataRepository {


    // TODO
    /**
     * Stream of [UserData]
     */
    val userData: Flow<DataState<com.victor.restart.data.UserData>>

    val authState: StateFlow<AuthState>

    val settingsState: StateFlow<AppSettings>

    suspend fun logOut(): DataState<String>
}