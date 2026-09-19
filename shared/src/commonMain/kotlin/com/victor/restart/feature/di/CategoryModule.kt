package com.victor.restart.feature.di

import com.victor.restart.feature.budget.BudgetViewModel
import com.victor.restart.feature.category.CategoryViewModel
import com.victor.restart.feature.home.HomeScreen
import com.victor.restart.feature.home.HomeViewModel
import com.victor.restart.feature.transaction.TransactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CategoryModule = module{
    viewModelOf(::CategoryViewModel)
    viewModelOf(::TransactionViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::BudgetViewModel)


}