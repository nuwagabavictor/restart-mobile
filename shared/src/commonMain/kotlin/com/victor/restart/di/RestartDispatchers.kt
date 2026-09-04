package com.victor.restart.di

import org.koin.core.annotation.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val restartDispatcher: RestartDispatchers)

enum class RestartDispatchers {
    Default,
    IO,
    Unconfined,
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope