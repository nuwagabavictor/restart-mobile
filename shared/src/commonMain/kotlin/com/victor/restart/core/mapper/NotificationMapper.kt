package com.victor.restart.core.mapper

import com.victor.restart.core.data.notification.NotificationDto
import com.victor.restart.core.entity.Notification

object NotificationMapper {

    fun NotificationDto.toDomain(): Notification {
        return Notification(
            id = id,
            notificationId = notificationId,
            userId = userId,
            isRead = isRead,
            createdAt = createdAt.toString(),
            message = notification.message,
            entity = notification.entity,
            action = notification.action,
            entityId = notification.entityId
        )
    }

    fun List<NotificationDto>.toDomain(): List<Notification> {
        return map { it.toDomain() }
    }
}