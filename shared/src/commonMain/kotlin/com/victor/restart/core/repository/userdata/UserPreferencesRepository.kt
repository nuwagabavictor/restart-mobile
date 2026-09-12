package com.victor.restart.core.repository.userdata

import com.victor.restart.core.enums.LanguageConfig
import com.victor.restart.core.enums.ThemeConfig
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.UserData
import com.victor.restart.data.AppSettings
import com.victor.restart.data.TimeBasedTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {

    val userInfo: StateFlow<UserData>

    val settingsInfo: StateFlow<AppSettings>

    val token: StateFlow<String?>


    val appTheme: StateFlow<ThemeConfig>

    val profileImage: String?

    val sentTokenToServer: StateFlow<Boolean>

    val gcmToken: StateFlow<String?>

    val observeLanguage: Flow<LanguageConfig>

    val observeDarkThemeConfig: Flow<ThemeConfig>

    val observeTimeBasedThemeConfig: Flow<TimeBasedTheme>

    val observeDynamicColorPreference: Flow<Boolean>

    val passcode: Flow<String>

    suspend fun updateToken(password: String): DataState<Unit>

    suspend fun updateTheme(theme: ThemeConfig): DataState<Unit>

    suspend fun updateTimeBasedTheme(theme: TimeBasedTheme): DataState<Unit>

    suspend fun updateUser(user: UserData): DataState<Unit>

    suspend fun updateSettings(appSettings: AppSettings): DataState<Unit>

    suspend fun updateProfileImage(image: String): DataState<Unit>


    suspend fun setSentTokenToServer(sent: Boolean): DataState<Unit>

    suspend fun saveGcmToken(token: String?): DataState<Unit>

    suspend fun setIsAuthenticated(isAuthenticated: Boolean)

    suspend fun setIsUnlocked(isUnlocked: Boolean)

    suspend fun setPasscode(passcode: String)

    suspend fun setShowOnboarding(showOnboarding: Boolean)

    suspend fun setFirstTimeState(firstTimeState: Boolean)

    suspend fun setLanguage(language: LanguageConfig)

    suspend fun setSelectedServices(selectedServices: Set<String>?)

    val selectedServices: Set<String>?
    fun saveSelectedServices(services: Set<String>?)

    suspend fun logOut(): Unit

}