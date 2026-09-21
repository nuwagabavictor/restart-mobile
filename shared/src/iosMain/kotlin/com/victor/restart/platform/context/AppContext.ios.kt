package com.victor.restart.platform.context

import androidx.compose.runtime.ProvidableCompositionLocal

actual abstract class AppContext

actual val LocalContext: ProvidableCompositionLocal<AppContext>
    get() = TODO("Not yet implemented")
actual val AppContext.activity: Any
    get() = TODO("Not yet implemented")