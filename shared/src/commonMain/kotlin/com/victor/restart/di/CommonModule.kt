package com.victor.restart.di

import org.koin.core.module.Module
import org.koin.dsl.module

val CommonModule = module {
    includes(dispatcherManagerModule)
}

expect val dispatcherManagerModule: Module