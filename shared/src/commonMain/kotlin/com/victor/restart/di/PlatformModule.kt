package com.victor.restart.di

import com.victor.restart.platform.garbage.GarbageCollectionManager
import com.victor.restart.platform.garbage.GarbageCollectionManagerImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val platformManagementModule = module {
    single<CoroutineDispatcher> { Dispatchers.Unconfined }
    single<GarbageCollectionManager> { GarbageCollectionManagerImpl(get()) }
}