package com.victor.restart.di

import com.victor.restart.core.utils.NetworkMonitor
import org.koin.core.module.Module

interface PlatformDependentDataModule {
    val networkMonitor: NetworkMonitor
}

expect val platformModule: Module

expect val getPlatformDataModule: PlatformDependentDataModule