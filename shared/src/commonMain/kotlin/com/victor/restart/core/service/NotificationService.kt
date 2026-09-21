package com.victor.restart.core.service


import com.victor.restart.core.ApiEndPoints
import com.victor.restart.core.data.notification.NotificationCountResponseDto
import com.victor.restart.core.data.notification.NotificationListResponseDto
import com.victor.restart.core.data.notification.NotificationReadResponseDto
import com.victor.restart.core.data.notification.NotificationResponseDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface NotificationService {

    @GET(ApiEndPoints.NOTIFICATION_COUNT)
    suspend fun getUnreadNotificationCount(): NotificationCountResponseDto

    @GET(ApiEndPoints.NOTIFICATION_UNREAD)
    suspend fun getUnreadNotifications(): NotificationListResponseDto

    @GET(ApiEndPoints.NOTIFICATIONS)
    suspend fun getNotifications(): NotificationListResponseDto

    @GET("${ApiEndPoints.NOTIFICATIONS}/{id}")
    suspend fun getNotification(@Path("id") id: Long): NotificationResponseDto

    @PUT("${ApiEndPoints.NOTIFICATIONS}/{notificationId}/read")
    suspend fun markNotificationAsRead(@Path("notificationId") notificationId: Long): NotificationReadResponseDto

    @PUT(ApiEndPoints.NOTIFICATION_READ_ALL)
    suspend fun markAllNotificationsAsRead(): NotificationReadResponseDto
}