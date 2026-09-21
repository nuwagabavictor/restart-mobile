package com.victor.restart.core.utils


import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale


actual object LocalAppLocale {

    private var defaultLocale: Locale? = null

    private val localAppLocale = staticCompositionLocalOf {
        Locale.getDefault().toLanguageTag()
    }

    actual val current: String
        @Composable
        get() = localAppLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {

        val configuration = LocalConfiguration.current

        if (defaultLocale == null) {
            defaultLocale = LocalLocale.current.platformLocale
        }

        val locale = when (value) {
            null -> defaultLocale!!
            else -> Locale.forLanguageTag(value)
        }

        Locale.setDefault(locale)

        configuration.setLocale(locale)

        val resources = LocalContext.current.resources

        resources.updateConfiguration(
            configuration,
            resources.displayMetrics,
        )

        return localAppLocale.provides(
            locale.toLanguageTag(),
        )
    }
}