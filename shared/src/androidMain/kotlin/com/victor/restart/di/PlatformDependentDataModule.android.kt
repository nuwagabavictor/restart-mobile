package com.victor.restart.di

import android.content.Context
import com.victor.restart.core.utils.ConnectivityManagerNetworkMonitor
import com.victor.restart.core.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineDispatcher

class AndroidPlatformDependentDataModule(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : PlatformDependentDataModule {
    override val networkMonitor: NetworkMonitor by lazy {
        ConnectivityManagerNetworkMonitor(context, dispatcher)
    }
}