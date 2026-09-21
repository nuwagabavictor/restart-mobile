package com.victor.restart.core.utils


import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

actual object LocalAppLocale {

    private val localAppLocale = staticCompositionLocalOf {
        Locale.getDefault().toLanguageTag()
    }

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        val locale = value?.let(Locale::forLanguageTag)
            ?: configuration.locales[0]

        val newConfiguration = Configuration(configuration).apply {
            setLocale(locale)
        }

        return localAppLocale.provides(
            newConfiguration.locales[0].toLanguageTag()
        )
    }
}