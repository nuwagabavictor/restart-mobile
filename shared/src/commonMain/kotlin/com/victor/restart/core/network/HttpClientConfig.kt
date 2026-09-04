package com.victor.restart.core.network

import com.victor.restart.core.utils.BaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Platform-specific HttpClient. Each platform (Android/iOS) supplies its own
 * engine (OkHttp / Darwin) plus any platform-only setup (e.g. logging).
 * Shared, cross-platform plugin config lives in [installCommonPlugins] below,
 * so it's written once and reused by every `actual` implementation.
 */
expect val ktorHttpClient: HttpClient

/**
 * Plugins that behave identically on every platform: timeouts + JSON
 * (de)serialization. Call this from each platform's `actual val ktorHttpClient`.
 */
fun HttpClientConfig<*>.installCommonPlugins() {
    install(HttpTimeout) {
        socketTimeoutMillis = 6000
        requestTimeoutMillis = 6000
        connectTimeoutMillis = 6000
    }

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                isLenient = true
            },
        )
    }

    install(DefaultRequest){

        url {
            host= BaseUrl.baseUrl
            headers {
                append(HttpHeaders.Accept, "")
            }
            contentType(ContentType.Application.Json)
        }
    }

    install(Logging){
        level = LogLevel.INFO
    }


}
