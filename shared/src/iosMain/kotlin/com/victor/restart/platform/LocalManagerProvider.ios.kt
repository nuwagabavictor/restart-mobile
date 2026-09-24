package com.victor.restart.platform

import androidx.compose.runtime.Composable
import com.victor.restart.platform.context.AppContext

@Composable
actual fun LocalManagerProvider(
    context: AppContext,
    content: @Composable (() -> Unit)
) {
}