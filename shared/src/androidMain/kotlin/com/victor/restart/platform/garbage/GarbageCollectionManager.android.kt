package com.victor.restart.platform.garbage

@Suppress("ExplicitGarbageCollectionCall")
actual val garbageCollector: () -> Unit
    get() = { Runtime.getRuntime().gc() }