package com.victor.restart.feature.notification


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("notification_graph")
data object NotificationGraph

@Serializable
@SerialName("notifications")
data object NotificationRoute

@Serializable
@SerialName("notification_detail")
data class NotificationDetailRoute(
    val notificationId: Long
)

fun NavController.navigateToNotifications() {
    navigate(NotificationRoute) {
        launchSingleTop = true
    }
}

fun NavController.navigateToNotificationDetail(
    notificationId: Long
) {
    navigate(NotificationDetailRoute(notificationId))
}

fun NavGraphBuilder.notificationDestination(
    navController: NavController
) {
    navigation<NotificationGraph>(
        startDestination = NotificationRoute
    ) {

        composable<NotificationRoute> {

            NotificationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationClick = { notificationId ->
                    navController.navigateToNotificationDetail(notificationId)
                }
            )
        }

        composable<NotificationDetailRoute> {

            val route = it.toRoute<NotificationDetailRoute>()

            NotificationDetailScreen(
                notificationId = route.notificationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}