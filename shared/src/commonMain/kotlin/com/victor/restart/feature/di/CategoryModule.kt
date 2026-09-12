package com.victor.restart.feature.di

import com.victor.restart.feature.category.CategoryViewModel
import com.victor.restart.feature.transaction.TransactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CategoryModule = module{
    viewModelOf(::CategoryViewModel)
    viewModelOf(::TransactionViewModel)

}