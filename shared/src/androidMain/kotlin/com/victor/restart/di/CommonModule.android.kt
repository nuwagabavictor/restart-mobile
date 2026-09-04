package com.victor.restart.di

import com.victor.restart.manager.DispatcherManagerImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dispatcherManagerModule: Module
    get() = module {
        single<DispatcherManager> { DispatcherManagerImpl() }
    }