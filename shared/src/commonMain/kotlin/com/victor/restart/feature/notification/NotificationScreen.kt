package com.victor.restart.feature.notification


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (Long) -> Unit,
    viewModel: NotificationViewModel = koinViewModel()
) {

    val state by viewModel.stateFlow.collectAsState()


    LaunchedEffect(Unit) {

        viewModel.eventFlow.collect { event ->

            when (event) {

                is NotificationEvent.NavigateToNotification -> {
                    onNotificationClick(event.notificationId)
                }

                NotificationEvent.NavigateBack -> onBackClick()

                is NotificationEvent.ShowToast -> {
                    // Handle snackbar/toast in your scaffold.
                }
            }
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {

                    IconButton(onClick = onBackClick) {

                        Text("←")
                    }
                },
                actions = {

                    if (state.notifications.isNotEmpty()) {

                        TextButton(
                            onClick = {
                                viewModel.trySendAction(
                                    NotificationAction.MarkAllAsReadClicked
                                )
                            }
                        ) {
                            Text("Read all")
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Badge
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Unread",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.width(8.dp))

                    Badge {
                        Text(state.unreadCount.toString())
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Filter
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                SegmentedButton(
                    selected = state.selectedFilter == NotificationFilter.ALL,
                    onClick = {
                        viewModel.trySendAction(
                            NotificationAction.FilterChanged(
                                NotificationFilter.ALL
                            )
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 2
                    )
                ) {
                    Text("All")
                }

                SegmentedButton(
                    selected = state.selectedFilter == NotificationFilter.UNREAD,
                    onClick = {
                        viewModel.trySendAction(
                            NotificationAction.FilterChanged(
                                NotificationFilter.UNREAD
                            )
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 2
                    )
                ) {
                    Text("Unread")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (state.filteredNotifications.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No notifications")
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        state.filteredNotifications,
                        key = { it.id }
                    ) { notification ->

                        NotificationItem(
                            notification = notification,
                            onClick = {

                                viewModel.trySendAction(
                                    NotificationAction.NotificationClicked(notification)
                                )
                            }
                        )
                    }
                }
            }
        }

        if (state.showOverlay) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }
    }
}