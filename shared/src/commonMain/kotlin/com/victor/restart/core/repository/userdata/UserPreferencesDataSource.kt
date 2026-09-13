

@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package com.victor.restart.core.repository.userdata



import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineDispatcher
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.victor.restart.core.enums.LanguageConfig
import com.victor.restart.core.enums.ThemeConfig
import com.victor.restart.core.utils.UserData
import com.victor.restart.data.AppSettings
import com.victor.restart.data.TimeBasedTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

private const val USER_DATA = "userData"
private const val APP_SETTINGS = "appSettings"

@Suppress("TooManyFunctions")
class UserPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher
) {

    private val _userInfo = MutableStateFlow(
        settings.decodeValue(
            key = USER_DATA,
            serializer = UserData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_DATA,
                serializer = UserData.serializer(),
            )?: UserData.DEFAULT
        )
    )

    private val _settingsInfo = MutableStateFlow(
        settings.decodeValue(
            key = APP_SETTINGS,
            serializer = AppSettings.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = APP_SETTINGS,
                serializer = AppSettings.serializer(),
            )?: AppSettings.DEFAULT
        )
    )

    val token = _userInfo.map {
        it.accessToken
    }

    val userInfo = _userInfo


    val settingsInfo = _settingsInfo


    val appTheme = _settingsInfo.map { it.appTheme }


    val passcode = _settingsInfo.map { it.passcode }

    val observeLanguage: Flow<LanguageConfig>
        get() = _settingsInfo.map { it.language }

    val observeDynamicColorPreference: Flow<Boolean>
        get() = _settingsInfo.map { it.useDynamicColor }

    val observeDarkThemeConfig: Flow<ThemeConfig>
        get() = _settingsInfo.map { it.appTheme }

    val observeTimeBasedThemeConfig: Flow<TimeBasedTheme>
        get() = _settingsInfo.map { it.timeBasedTheme }

    suspend fun updateSettingsInfo(appSettings: AppSettings) {
        withContext(dispatcher) {
            settings.putSettingsPreference(appSettings)
            _settingsInfo.value = appSettings
        }
    }

    suspend fun updateUserInfo(user: UserData) {
        withContext(dispatcher) {
            settings.putUserPreference(user)
            _userInfo.value = user
        }
    }

    suspend fun updateToken(token: String?) {
        withContext(dispatcher) {
            val updatedClient = userInfo.value.copy(
                username = _userInfo.value.username,
                isAuthenticated = _userInfo.value.isAuthenticated,
                accessToken = token,
            )
            settings.putUserPreference(updatedClient)
            _userInfo.value = updatedClient
        }
    }


    suspend fun updateTheme(theme: ThemeConfig) {
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(appTheme = theme)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }
    }

    suspend fun updateTimeBasedTheme(timeBasedTheme: TimeBasedTheme) {
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(timeBasedTheme = timeBasedTheme)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }
    }

    fun updateProfileImage(image: String) {
        settings.putString(PROFILE_IMAGE, image)
    }

    fun getProfileImage(): String? {
        return settings.getString(PROFILE_IMAGE, "").ifEmpty { null }
    }

    suspend fun clearInfo() {
        withContext(dispatcher) {
            settings.putUserPreference(UserData.DEFAULT)
            _userInfo.value = UserData.DEFAULT
            val cleared = settings.getSettingsPreference().copy(
                isAuthenticated = false,
            )
            settings.putSettingsPreference(cleared)
            _settingsInfo.value = cleared
        }
    }

    suspend fun setSentTokenToServer(sent: Boolean) {
        withContext(dispatcher) {
            val updatedSettings = _settingsInfo.value.copy(sentTokenToServer = sent)
            settings.putSettingsPreference(updatedSettings)
            _settingsInfo.value = updatedSettings
        }
    }

    fun isSentTokenToServer(): Boolean {
        return _settingsInfo.value.sentTokenToServer
    }

    suspend fun saveGcmToken(token: String?) {
        withContext(dispatcher) {
            val updatedSettings = _settingsInfo.value.copy(gcmToken = token)
            settings.putSettingsPreference(updatedSettings)
            _settingsInfo.value = updatedSettings
        }
    }

    fun getGcmToken(): String? {
        return _settingsInfo.value.gcmToken
    }

    suspend fun setLanguage(language: LanguageConfig) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(language = language)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setShowOnboarding(showOnboarding: Boolean) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(showOnboarding = showOnboarding)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setFirstTimeState(firstTimeState: Boolean) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(firstTimeState = firstTimeState)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setIsAuthenticated(isAuthenticated: Boolean) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(isAuthenticated = isAuthenticated)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setIsUnlocked(isUnlocked: Boolean) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(isUnlocked = isUnlocked)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setPasscode(passcode: String) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(passcode = passcode)
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    suspend fun setSelectedServices(selectedServices: Set<String>?) =
        withContext(dispatcher) {
            val newPreference = settings.getSettingsPreference().copy(selectedServices = selectedServices ?: emptySet())
            settings.putSettingsPreference(newPreference)
            _settingsInfo.value = newPreference
        }

    fun saveSelectedServicesDirectly(services: Set<String>?) {
        if (services == null) {
            settings.remove(SELECTED_SERVICES_KEY)
        } else {
            settings.putString(SELECTED_SERVICES_KEY, services.joinToString(","))
        }
        val newPreference = settings.getSettingsPreference().copy(selectedServices = services ?: emptySet())
        _settingsInfo.value = newPreference
    }

    fun getSelectedServicesDirectly(): Set<String>? {
        val directString = settings.getStringOrNull(SELECTED_SERVICES_KEY)
        return if (directString == null) {
            null
        } else if (directString.isBlank()) {
            emptySet()
        } else {
            directString.split(",").filter { it.isNotBlank() }.toSet()
        }
    }

    companion object {
        private const val PROFILE_IMAGE = "preferences_profile_image"
        private const val SELECTED_SERVICES_KEY = "selected_services_list"
    }
}


@OptIn(ExperimentalSerializationApi::class)
private fun Settings.putUserPreference(user: UserData) {
    encodeValue(
        key = USER_DATA,
        serializer = UserData.serializer(),
        value = user,
    )
}

private fun Settings.getSettingsPreference(): AppSettings {
    return decodeValue(
        key = APP_SETTINGS,
        serializer = AppSettings.serializer(),
        defaultValue = AppSettings.DEFAULT,
    )
}

private fun Settings.putSettingsPreference(settings: AppSettings) {
    encodeValue(
        key = APP_SETTINGS,
        serializer = AppSettings.serializer(),
        value = settings,
    )
}
