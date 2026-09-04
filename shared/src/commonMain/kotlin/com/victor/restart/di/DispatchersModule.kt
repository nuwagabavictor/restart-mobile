package com.victor.restart.di

import com.victor.restart.core.utils.DefaultStringProvider
import com.victor.restart.core.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val stringProviderModule = module {
    single<StringProvider> { DefaultStringProvider() }
}

val DispatchersModule = module {
    includes(ioDispatcherModule)
    single<CoroutineDispatcher>(named(RestartDispatchers.Default.name)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(RestartDispatchers.Unconfined.name)) { Dispatchers.Unconfined }
    single<CoroutineScope>(named("ApplicationScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}

expect val ioDispatcherModule: Module