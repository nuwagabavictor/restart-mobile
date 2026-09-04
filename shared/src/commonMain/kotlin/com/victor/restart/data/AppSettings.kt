package com.victor.restart.data

import com.victor.restart.core.enums.LanguageConfig
import com.victor.restart.core.enums.ThemeConfig
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val baseUrl: String,
    val passcode: String,
    val appTheme: ThemeConfig,
    val sentTokenToServer: Boolean = false,
    val gcmToken: String? = null,
    val useDynamicColor: Boolean,
    val isAuthenticated: Boolean,
    val isUnlocked: Boolean,
    val language: LanguageConfig,
    val showOnboarding: Boolean,
    val firstTimeState: Boolean,
    val timeBasedTheme: TimeBasedTheme,
    val selectedServices: Set<String> = emptySet(),
) {
    companion object {
        val DEFAULT = AppSettings(
            baseUrl = "https://localhost:8000/",
            appTheme = ThemeConfig.FOLLOW_SYSTEM,
            sentTokenToServer = false,
            gcmToken = null,
            language = LanguageConfig.DEFAULT,
            showOnboarding = true,
            firstTimeState = true,
            useDynamicColor = false,
            isAuthenticated = false,
            passcode = "",
            isUnlocked = false,
            timeBasedTheme = TimeBasedTheme(hourStart = 6, hourEnd = 0, timeStart = 17, timeEnd = 59),
        )
    }
}