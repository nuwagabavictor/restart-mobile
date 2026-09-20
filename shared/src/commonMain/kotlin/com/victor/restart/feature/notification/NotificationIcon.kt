package com.victor.restart.feature.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationIcon(
    unreadCount: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge {
                        Box(
                            modifier = Modifier.size(18.dp)
                        ) {
                            androidx.compose.material3.Text(
                                text = if (unreadCount > 99) {
                                    "99+"
                                } else {
                                    unreadCount.toString()
                                }
                            )
                        }
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}