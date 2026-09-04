package com.victor.restart.core.network

import androidx.compose.ui.window.ComposeUIViewController
import com.victor.restart.App
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

