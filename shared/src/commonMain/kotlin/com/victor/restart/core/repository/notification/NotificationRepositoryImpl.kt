package com.victor.restart.core.repository.notification


import com.victor.restart.core.entity.Notification
import com.victor.restart.core.mapper.NotificationMapper.toDomain
import com.victor.restart.core.network.DataManager
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
    private val dataManager: DataManager,
    private val ioDispatcher: CoroutineDispatcher
) : NotificationRepository {

    override fun getUnreadNotificationCount(): Flow<DataState<Int>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.getUnreadNotificationCount()
            }

            Logger.d("NotificationRepository", "Unread count: ${response.count}")

            emit(DataState.Success(response.count))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Get unread notification count failed", e)


            emit(DataState.Error(e))
        }
    }

    override fun getUnreadNotifications(): Flow<DataState<List<Notification>>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.getUnreadNotifications()
            }

            val notifications = response.notifications.toDomain()

            Logger.d("NotificationRepository", "Unread notifications: $notifications")

            emit(DataState.Success(notifications))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Get unread notifications failed", e)

            emit(DataState.Error(e))
        }
    }

    override fun getNotifications(): Flow<DataState<List<Notification>>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.getNotifications()
            }

            val notifications = response.notifications.toDomain()

            Logger.d("NotificationRepository", "Notifications: $notifications")

            emit(DataState.Success(notifications))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Get notifications failed", e)

            emit(DataState.Error(e))
        }
    }

    override fun getNotification(
        id: Long
    ): Flow<DataState<Notification>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.getNotification(id)
            }

            val notification = response.notification.toDomain()

            Logger.d("NotificationRepository", "Notification: $notification")

            emit(DataState.Success(notification))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Get notification failed", e)

            emit(DataState.Error(e))
        }
    }

    override fun markNotificationAsRead(
        notificationId: Long
    ): Flow<DataState<String>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.markNotificationAsRead(notificationId)
            }

            Logger.d("NotificationRepository", "Mark notification as read: ${response.message}")

            emit(DataState.Success(response.message))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Mark notification as read failed", e)

            emit(DataState.Error(e))
        }
    }

    override fun markAllNotificationsAsRead(): Flow<DataState<String>> = flow {
        emit(DataState.Loading)

        try {
            val response = withContext(ioDispatcher) {
                dataManager.notificationApi.markAllNotificationsAsRead()
            }

            Logger.d("NotificationRepository", "Mark all notifications as read: ${response.message}")

            emit(DataState.Success(response.message))
        } catch (e: Exception) {
            Logger.e("NotificationRepository", "Mark all notifications as read failed", e)

            emit(DataState.Error(e))
        }
    }
}