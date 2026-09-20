package com.victor.restart.feature.notification


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.victor.restart.core.entity.Notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor =
                if (!notification.isRead)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = when (notification.action) {
                    "USER_LOGIN" -> Icons.Outlined.Login
                    "TRANSACTION_CREATED" -> Icons.Outlined.Payments
                    else -> Icons.Outlined.Notifications
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight =
                        if (!notification.isRead)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notification.entity,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = notification.createdAt,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!notification.isRead) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}