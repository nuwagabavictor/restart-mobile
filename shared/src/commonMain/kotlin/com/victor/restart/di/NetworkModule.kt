package com.victor.restart.di

import com.victor.restart.core.network.DataManager
import com.victor.restart.core.network.KtorInterceptor
import com.victor.restart.core.network.KtorfitClient
import com.victor.restart.core.network.ktorHttpClient
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.utils.BaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import org.koin.dsl.module

val NetworkModule = module {


    single<HttpClient>(KtorClient) {
        val preferencesRepository = get<UserPreferencesRepository>()

        ktorHttpClient.config {
            install(Auth)
            install(KtorInterceptor) {
                getToken = { preferencesRepository.token.value }

                onUnauthorized = suspend {
                    preferencesRepository.logOut()
                }
            }
        }
    }

    single<KtorfitClient>(MifosClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorClient))
            .baseURL(BaseUrl.baseUrl)
            .build()
    }

    single {
        DataManager(ktorfitClient = get(MifosClient))
    }
}