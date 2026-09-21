package com.victor.restart.feature.notification


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationDetailScreen(
    notificationId: Long,
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = koinViewModel()
) {

    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(notificationId) {

        viewModel.trySendAction(
            NotificationAction.LoadNotification(notificationId)
        )
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = { Text("Notification") },
                navigationIcon = {

                    IconButton(onClick = onBackClick) {

                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        val notification = state.selectedNotification

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (notification == null) {

                if (state.showOverlay) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Icon(
                        imageVector = when (notification.action) {
                            "USER_LOGIN" -> Icons.Outlined.Login
                            "TRANSACTION_CREATED" -> Icons.Outlined.Payments
                            else -> Icons.Outlined.Notifications
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider()

                    DetailRow(
                        title = "Entity",
                        value = notification.entity
                    )

                    DetailRow(
                        title = "Action",
                        value = notification.action
                    )

                    DetailRow(
                        title = "Entity ID",
                        value = notification.entityId.toString()
                    )

                    DetailRow(
                        title = "Date",
                        value = notification.createdAt
                    )

                    DetailRow(
                        title = "Status",
                        value = if (notification.isRead)
                            "Read"
                        else
                            "Unread"
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String
) {

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}