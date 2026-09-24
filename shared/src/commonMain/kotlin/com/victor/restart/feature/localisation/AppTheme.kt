package com.victor.restart.feature.localisation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.victor.restart.core.enums.ThemeConfig

@Composable
fun AppTheme(
    theme: ThemeConfig,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (theme) {
        ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeConfig.LIGHT -> false
        ThemeConfig.DARK -> true
        ThemeConfig.BASED_ON_TIME -> false
    }

    MaterialTheme(
        colorScheme = if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        },
        content = content,
    )
}