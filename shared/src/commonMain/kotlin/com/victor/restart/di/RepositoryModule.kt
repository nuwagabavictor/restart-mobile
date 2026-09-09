package com.victor.restart.di

import com.victor.restart.core.repository.CategoryRepository
import com.victor.restart.core.repository.CategoryRepositoryImpl
import com.victor.restart.core.repository.UserDataRepository
import com.victor.restart.core.repository.UserDataRepositoryImpl
import com.victor.restart.core.repository.UserRepository
import com.victor.restart.core.repository.UserRepositoryImp
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val ioDispatcher = named(RestartDispatchers.IO.name)
private val unconfinedDispatcher = named(RestartDispatchers.Unconfined.name)

val RepositoryModule = module {
    single<Json> { Json { ignoreUnknownKeys = true } }

    single< UserRepository> { UserRepositoryImp(get(), get(ioDispatcher)) }

    single<UserDataRepository> { UserDataRepositoryImpl(get(), get(ioDispatcher), get(unconfinedDispatcher)) }
    single<CategoryRepository>{ CategoryRepositoryImpl(get(), get(ioDispatcher)) }


}