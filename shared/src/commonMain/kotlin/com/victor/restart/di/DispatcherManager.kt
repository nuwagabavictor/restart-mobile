package com.victor.restart.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher

interface DispatcherManager {
    /**
     * The default [CoroutineDispatcher] for the app.
     */
    val default: CoroutineDispatcher

    /**
     * The [MainCoroutineDispatcher] for the app.
     */
    val main: MainCoroutineDispatcher

    /**
     * The IO [CoroutineDispatcher] for the app.
     */
    val io: CoroutineDispatcher

    /**
     * The unconfined [CoroutineDispatcher] for the app.
     */
    val unconfined: CoroutineDispatcher

    val appScope: CoroutineScope
}