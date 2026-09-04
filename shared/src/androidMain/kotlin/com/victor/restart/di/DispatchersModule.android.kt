package com.victor.restart.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val ioDispatcherModule: Module
    get() = module {
        single<CoroutineDispatcher>(named(RestartDispatchers.IO.name)) { Dispatchers.IO }
    }