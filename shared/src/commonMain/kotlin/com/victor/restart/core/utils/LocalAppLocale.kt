package com.victor.restart.core.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

expect object LocalAppLocale {

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}