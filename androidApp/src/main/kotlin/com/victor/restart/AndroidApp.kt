package com.victor.restart

import android.app.Application
import com.victor.restart.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

/**
 * Android application class.
 * This class is used to initialize Koin modules for dependency injection in the Android application.
 * It sets up the Koin framework, providing the necessary dependencies for the app.
 *
 * @constructor Create empty Android app
 * @see Application
 */

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApp) // Provides the Android app context
            androidLogger(Level.DEBUG) // Enables Koin's logging for debugging
        }
    }
}