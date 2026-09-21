package com.victor.restart.platform.garbage

interface GarbageCollectionManager {
    /**
     * Calls the garbage collector on the [Runtime] in an effort to clear the unused resources in
     * the heap.
     */
    fun tryCollect()
}

expect val garbageCollector: () -> Unit