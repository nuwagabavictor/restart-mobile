package com.victor.restart.core.data.notification


import com.victor.restart.core.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Long,

    @SerialName("notificationId")
    val notificationId: Long,

    @SerialName("userId")
    val userId: Long,

    @SerialName("isRead")
    val isRead: Boolean,

    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,

    val notification: NotificationDetailsDto
)

@Serializable
data class NotificationDetailsDto(
    val id: Long,
    val message: String,
    val entity: String,
    val action: String,
    val entityId: Long
)