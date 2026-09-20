package com.victor.restart.di

import com.victor.restart.core.repository.budget.BudgetRepository
import com.victor.restart.core.repository.budget.BudgetRepositoryImpl
import com.victor.restart.core.repository.category.CategoryRepository
import com.victor.restart.core.repository.category.CategoryRepositoryImpl
import com.victor.restart.core.repository.notification.NotificationRepository
import com.victor.restart.core.repository.notification.NotificationRepositoryImpl
import com.victor.restart.core.repository.transaction.TransactionRepository
import com.victor.restart.core.repository.transaction.TransactionRepositoryImpl
import com.victor.restart.core.repository.userdata.UserDataRepository
import com.victor.restart.core.repository.userdata.UserDataRepositoryImpl
import com.victor.restart.core.repository.user.UserRepository
import com.victor.restart.core.repository.user.UserRepositoryImp
import com.victor.restart.core.utils.NetworkMonitor
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
    single<TransactionRepository>{ TransactionRepositoryImpl(get(), get(ioDispatcher)) }
    single<BudgetRepository>{ BudgetRepositoryImpl(get(), get(ioDispatcher)) }
    single<NotificationRepository>{ NotificationRepositoryImpl(get(), get(ioDispatcher)) }
    single<PlatformDependentDataModule> { getPlatformDataModule }
    single<NetworkMonitor> { getPlatformDataModule.networkMonitor }
    includes(platformModule)



}