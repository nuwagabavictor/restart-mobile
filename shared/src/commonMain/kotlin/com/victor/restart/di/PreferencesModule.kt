package com.victor.restart.di

import com.russhwolf.settings.Settings
import com.victor.restart.core.repository.userdata.UserPreferencesDataSource
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.repository.userdata.UserPreferencesRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val PreferencesModule = module {
    single<Settings> { Settings() }

    single<UserPreferencesDataSource> {
        UserPreferencesDataSource(
            settings = get(),
            dispatcher = get(named(RestartDispatchers.IO.name)),
        )
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            preferenceManager = get(),
            unconfinedDispatcher = get(named(RestartDispatchers.Unconfined.name)),
        )
    }
}