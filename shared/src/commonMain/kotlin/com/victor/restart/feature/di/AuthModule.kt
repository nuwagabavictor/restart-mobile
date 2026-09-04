package com.victor.restart.feature.di

import com.victor.restart.feature.login.LoginViewModel
import com.victor.restart.feature.password.PasswordViewModel
import com.victor.restart.feature.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val AuthModule = module{

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PasswordViewModel)
}