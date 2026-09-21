package com.victor.restart.core.data.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseDto(
    val notification: NotificationDto
)

@Serializable
data class NotificationListResponseDto(
    val notifications: List<NotificationDto>
)

@Serializable
data class NotificationCountResponseDto(
    val count: Int
)

@Serializable
data class NotificationReadResponseDto(
    val message: String
)
