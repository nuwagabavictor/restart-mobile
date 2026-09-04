package com.victor.restart.core.utils

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json


/**
 * Generic function to extract error messages from API responses
 */
suspend fun extractErrorMessage(response: HttpResponse): String {
    val responseText = response.bodyAsText()
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val errorResponse = json.decodeFromString<AppError>(responseText)
        errorResponse.message
            ?: errorResponse.httpStatusCode
            ?: "Unknown error"
    } catch (e: Exception) {
        "Failed to parse error response"
    }
}