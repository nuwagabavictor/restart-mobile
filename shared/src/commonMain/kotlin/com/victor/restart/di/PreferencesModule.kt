package com.victor.restart.di

import com.russhwolf.settings.Settings
import com.victor.restart.core.repository.CategoryRepository
import com.victor.restart.core.repository.CategoryRepositoryImpl
import com.victor.restart.core.repository.UserPreferencesDataSource
import com.victor.restart.core.repository.UserPreferencesRepository
import com.victor.restart.core.repository.UserPreferencesRepositoryImpl
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