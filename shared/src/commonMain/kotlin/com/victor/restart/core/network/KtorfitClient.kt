package com.victor.restart.core.network

import com.victor.restart.core.service.createCategoryService
import com.victor.restart.core.service.createTransactionService
import com.victor.restart.core.service.createUserService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

/**
 * Single injection point for every Ktorfit-generated API interface.
 * ViewModels/repositories depend on this instead of individual services,
 * so adding a new endpoint interface later means adding one lazy property
 * here rather than a new Koin binding + a new constructor parameter everywhere
 * that needs it.
 */

class KtorfitClient(
      ktorfit: Ktorfit
) {

    internal val userApi by lazy { ktorfit.createUserService() }

    internal val categoryApi by lazy { ktorfit.createCategoryService() }

    internal val transactionApi by lazy { ktorfit.createTransactionService() }


    class Builder internal constructor() {
        private lateinit var baseURL: String
        private lateinit var httpClient: HttpClient

        fun baseURL(baseURL: String): Builder {
            this.baseURL = baseURL
            return this
        }

        fun httpClient(ktorHttpClient: HttpClient): Builder {
            this.httpClient = ktorHttpClient
            return this
        }

        fun build(): KtorfitClient {
            val ktorfitBuilder = Ktorfit.Builder()
                .httpClient(httpClient)
                .baseUrl(baseURL)
                .converterFactories(FlowConverterFactory())
                .build()

            return KtorfitClient(ktorfitBuilder)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
