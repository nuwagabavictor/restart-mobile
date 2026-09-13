package com.victor.restart.core.network



open class DataManager(
    private val ktorfitClient: KtorfitClient? = null
) {
    open val userApi by lazy { ktorfitClient!!.userApi }
    open val categoryApi by lazy { ktorfitClient!!.categoryApi }

    open val transactionApi by lazy { ktorfitClient!!.transactionApi }

}