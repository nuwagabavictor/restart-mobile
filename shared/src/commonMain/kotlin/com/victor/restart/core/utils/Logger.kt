package com.victor.restart.core.utils

object Logger {

    fun d(tag: String, message: String) {
        println("DEBUG [$tag] $message")
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        println("ERROR [$tag] $message")
        throwable?.printStackTrace()
    }
}
