package com.victor.restart.core.utils

/**
 * Thrown when a network operation is attempted without a stable, validated
 * internet connection.
 *
 * @param message Human-readable reason shown in logs and crash reports.
 * @param cause   Optional underlying throwable.
 */
class NetworkUnavailableException(
    message: String = "No stable network connection available",
    cause: Throwable? = null,
) : IllegalStateException(message, cause)
