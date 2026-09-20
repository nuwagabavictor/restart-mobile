package com.victor.restart.core.repository.notification


import com.victor.restart.core.entity.Notification
import com.victor.restart.core.utils.DataState
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getUnreadNotificationCount(): Flow<DataState<Int>>

    fun getUnreadNotifications(): Flow<DataState<List<Notification>>>

    fun getNotifications(): Flow<DataState<List<Notification>>>

    fun getNotification(id: Long): Flow<DataState<Notification>>

    fun markNotificationAsRead(notificationId: Long): Flow<DataState<String>>

    fun markAllNotificationsAsRead(): Flow<DataState<String>>
}