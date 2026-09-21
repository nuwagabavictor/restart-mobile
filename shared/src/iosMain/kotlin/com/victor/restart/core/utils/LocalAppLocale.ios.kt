package com.victor.restart.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue


import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual object LocalAppLocale {

    private const val LANGUAGE_KEY = "AppleLanguages"

    private val defaultLocale =
        NSLocale.preferredLanguages.firstOrNull()?.toString() ?: "en"

    private val localAppLocale = staticCompositionLocalOf {
        defaultLocale
    }


    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {

        val locale = value ?: defaultLocale

        if (value == null) {
            NSUserDefaults.standardUserDefaults
                .removeObjectForKey(LANGUAGE_KEY)
        } else {
            NSUserDefaults.standardUserDefaults
                .setObject(
                    arrayListOf(locale),
                    LANGUAGE_KEY,
                )
        }

        return localAppLocale.provides(locale)
    }
}