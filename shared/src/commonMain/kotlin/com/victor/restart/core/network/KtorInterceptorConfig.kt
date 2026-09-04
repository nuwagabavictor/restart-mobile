package com.victor.restart.core.network


import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

/**
 * Configuration surface for [KtorInterceptor]. Deliberately holds only
 * plain lambdas rather than a concrete repository type, so this plugin
 * has zero knowledge of where the token comes from or what "logged out"
 * means for the app — that's supplied by whoever installs it (see
 * NetworkModule.kt), same idea as a functional interface callback in Java.
 */
class KtorInterceptorConfig {
    var getToken: () -> String? = { null }
    var onUnauthorized: suspend () -> Unit = {}
}

/**
 * Attaches a Bearer token (when available) to every outgoing request,
 * and reacts to 401 responses by invoking [KtorInterceptorConfig.onUnauthorized]
 * (e.g. to clear stored credentials and force a re-login).
 */
val KtorInterceptor = createClientPlugin("KtorInterceptor", ::KtorInterceptorConfig) {
    val getToken = pluginConfig.getToken
    val onUnauthorized = pluginConfig.onUnauthorized

    onRequest { request, _ ->
        request.contentType(ContentType.Application.Json)

        request.header(HttpHeaders.Accept, "application/json")

        getToken()?.takeIf { it.isNotBlank() }?.let { token ->
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    onResponse { response ->
        if (response.status.value == 401) {
            onUnauthorized()
        }
    }
}
