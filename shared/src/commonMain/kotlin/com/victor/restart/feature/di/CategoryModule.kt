package com.victor.restart.feature.di

import com.victor.restart.feature.budget.BudgetViewModel
import com.victor.restart.feature.category.CategoryViewModel
import com.victor.restart.feature.home.HomeScreen
import com.victor.restart.feature.home.HomeViewModel
import com.victor.restart.feature.language.ChangeLanguageViewModel
import com.victor.restart.feature.notification.NotificationViewModel
import com.victor.restart.feature.password.PasswordViewModel
import com.victor.restart.feature.settings.SettingsViewModel
import com.victor.restart.feature.theme.ChangeThemeViewModel
import com.victor.restart.feature.transaction.TransactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val CategoryModule = module{
    viewModelOf(::CategoryViewModel)
    viewModelOf(::TransactionViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::BudgetViewModel)
    viewModelOf(::NotificationViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ChangeLanguageViewModel)
    viewModelOf(::ChangeThemeViewModel)
    viewModelOf(::PasswordViewModel)


}