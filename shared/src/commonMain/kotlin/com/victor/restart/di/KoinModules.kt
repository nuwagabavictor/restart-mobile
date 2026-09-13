package com.victor.restart.di

import com.victor.restart.feature.di.AuthModule
import com.victor.restart.feature.di.CategoryModule
import org.koin.dsl.module


object KoinModules {

    private val commonModules = module {
        includes(DispatchersModule)
        includes(stringProviderModule)
        includes(CommonModule)
    }

    private val dataModules = module {
        includes(RepositoryModule)
    }
    private val networkModules = module {
        includes(NetworkModule)
    }
    private val featureModules = module {
        includes(
            AuthModule,
            CategoryModule
        )
    }

    val allModules = listOf(
        commonModules,
        dataModules,
        PreferencesModule,
        networkModules,
        featureModules,
    )
}