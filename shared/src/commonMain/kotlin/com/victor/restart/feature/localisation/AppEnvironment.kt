package com.victor.restart.feature.localisation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import com.victor.restart.core.utils.LocalAppLocale

@Composable
fun AppEnvironment(
    locale: String?,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppLocale provides locale,
    ) {
        key(locale) {
            content()
        }
    }
}