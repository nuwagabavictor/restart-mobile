package com.victor.restart.core.entity

import kotlinx.datetime.LocalDateTime

data class Notification(
    val id: Long,
    val notificationId: Long,
    val userId: Long,
    val isRead: Boolean,
    val createdAt: String,
    val message: String,
    val entity: String,
    val action: String,
    val entityId: Long
)
