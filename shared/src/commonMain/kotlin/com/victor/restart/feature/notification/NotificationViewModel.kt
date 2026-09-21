package com.victor.restart.feature.notification


import androidx.lifecycle.SavedStateHandle
import com.victor.restart.core.entity.Notification
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState

import androidx.lifecycle.viewModelScope
import com.victor.restart.core.repository.notification.NotificationRepository
import com.victor.restart.core.utils.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    savedStateHandle: SavedStateHandle

) : BaseViewModel<NotificationState, NotificationEvent, NotificationAction>(
    initialState = NotificationState(uiState = ScreenUiState.Success)
) {

    private var notificationsJob: Job? = null
    private var unreadCountJob: Job? = null
    private var markAllAsReadJob: Job? = null
    private var loadNotificationJob: Job? = null
    private var markAsReadJob: Job? = null

    init {

       var id =  savedStateHandle.get<Long>("notificationId");

        if (id != null && id > 0L) {
            println("NOTIFICATION DETAIL VM: loading id=$id")
            trySendAction(NotificationAction.LoadNotification(id))
        }else{
            println("NOTIFICATION VM: initialized")

            trySendAction(NotificationAction.LoadNotifications)

            trySendAction(NotificationAction.LoadUnreadCount)
        }


    }

    private fun updateState(block: (NotificationState) -> NotificationState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: NotificationAction) {

        when (action) {

            NotificationAction.LoadNotifications -> {
                loadNotifications()
            }

            is NotificationAction.LoadNotification -> {
                loadNotification(action.id)
            }

            NotificationAction.LoadUnreadCount -> {
                loadUnreadCount()
            }

            is NotificationAction.FilterChanged -> {

                updateState {
                    it.copy(
                        selectedFilter = action.filter
                    )
                }

                loadNotifications()
            }

            is NotificationAction.NotificationClicked -> {

                sendEvent(
                    NotificationEvent.NavigateToNotification(
                        notificationId = action.notification.id
                    )
                )
            }


            NotificationAction.MarkAllAsReadClicked -> {
                markAllAsRead()
            }


            NotificationAction.ErrorDialogDismiss -> {

                updateState {
                    it.copy(
                        dialogState = null,
                        isError = false
                    )
                }
            }


            is NotificationAction.Internal.ReceiveNotificationsResult -> {
                handleNotificationsResult(action.result)
            }

            is NotificationAction.Internal.ReceiveUnreadCountResult -> {
                handleUnreadCountResult(action.result)
            }

            is NotificationAction.Internal.ReceiveMarkAllAsReadResult -> {
                handleMarkAllAsReadResult(action.result)
            }

            is NotificationAction.Internal.ReceiveNotificationResult -> {
                handleNotificationResult(action.result)
            }

            is NotificationAction.Internal.ReceiveMarkAsReadResult -> {
                handleMarkAsReadResult(action.result)
            }
        }
    }


    private fun loadNotifications() {

        notificationsJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        notificationsJob = viewModelScope.launch {

            val notificationsFlow = when (state.selectedFilter) {

                NotificationFilter.ALL -> { notificationRepository.getNotifications() }

                NotificationFilter.UNREAD -> { notificationRepository.getUnreadNotifications() }
            }

            notificationsFlow.collect { result ->

                println("NOTIFICATION VM: notifications result = $result")

                sendAction(
                    NotificationAction.Internal.ReceiveNotificationsResult(result)
                )
            }
        }
    }

    private fun handleNotificationsResult(result: DataState<List<Notification>>) {

        when (result) {

            is DataState.Loading -> {

                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        notifications = result.data,
                        showOverlay = false,
                        isError = false,
                        dialogState = null
                    )
                }
            }

            is DataState.Error -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState =
                            NotificationState.DialogState.Error(
                                result.message
                            )
                    )
                }
            }
        }
    }

    private fun loadNotification(id: Long) {

        loadNotificationJob?.cancel()

        updateState {
            it.copy(
                id = id,
                showOverlay = true
            )
        }

        loadNotificationJob = viewModelScope.launch {

            notificationRepository.getNotification(id)
                .collect { result ->
                    sendAction(NotificationAction.Internal.ReceiveNotificationResult(result))
                }

        }
    }

    private fun handleNotificationResult(result: DataState<Notification>) {

        when (result) {

            is DataState.Loading -> {

                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        selectedNotification = result.data,
                        showOverlay = false,
                        isError = false,
                        dialogState = null
                    )
                }

                // Automatically mark it as read when opened.
                if (!result.data.isRead) {
                    markAsRead(result.data.id)
                }
            }

            is DataState.Error -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState =
                            NotificationState.DialogState.Error(
                                result.message
                            )
                    )
                }
            }
        }
    }

    private fun markAsRead(notificationId: Long) {

        markAsReadJob?.cancel()

        markAsReadJob = viewModelScope.launch {

            notificationRepository.markNotificationAsRead(notificationId)
                .collect { result ->
                    sendAction(NotificationAction.Internal.ReceiveMarkAsReadResult(result))
                }
        }
    }

    private fun handleMarkAsReadResult(result: DataState<String>) {

        when (result) {

            is DataState.Loading -> {
                // Do not cover the detail page with an overlay.
                // The notification is already being displayed.
            }

            is DataState.Success -> {

                val currentNotification = state.selectedNotification

                if (currentNotification != null) {

                    updateState {
                        it.copy(
                            selectedNotification =
                                currentNotification.copy(
                                    isRead = true
                                )
                        )
                    }
                }

                // Refresh notification list.
                loadNotifications()

                // Refresh notification badge.
                loadUnreadCount()

                println("NOTIFICATION DETAIL VM: ${result.data}")
            }

            is DataState.Error -> {

                println("NOTIFICATION DETAIL VM: mark as read failed = ${result.message}")
                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = NotificationState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }


    private fun loadUnreadCount() {

        unreadCountJob?.cancel()

        unreadCountJob = viewModelScope.launch {

            notificationRepository
                .getUnreadNotificationCount()
                .collect { result ->

                    println("NOTIFICATION VM: unread count result = $result")

                    sendAction(
                        NotificationAction.Internal.ReceiveUnreadCountResult(result)
                    )
                }
        }
    }

    private fun handleUnreadCountResult(result: DataState<Int>) {

        when (result) {

            is DataState.Loading -> {
                // Do not show the full-screen overlay
                // just because the badge count is loading.
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        unreadCount = result.data
                    )
                }
            }

            is DataState.Error -> {
                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = NotificationState.DialogState.Error(result.message)
                    )
                }
                println("NOTIFICATION VM: unread count error = ${result.message}")
            }
        }
    }


    private fun markAllAsRead() {

        markAllAsReadJob?.cancel()

        markAllAsReadJob = viewModelScope.launch {

            notificationRepository
                .markAllNotificationsAsRead()
                .collect { result ->

                    sendAction(
                        NotificationAction.Internal.ReceiveMarkAllAsReadResult(result)
                    )
                }
        }
    }

    private fun handleMarkAllAsReadResult(result: DataState<String>) {

        when (result) {

            is DataState.Loading -> {

                updateState {
                    it.copy(
                        showOverlay = true
                    )
                }
            }

            is DataState.Success -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = false,
                        dialogState = null
                    )
                }

                sendEvent(NotificationEvent.ShowToast(result.data))

                // Refresh notification list.
                loadNotifications()

                // Refresh notification badge.
                loadUnreadCount()
            }

            is DataState.Error -> {

                updateState {
                    it.copy(
                        showOverlay = false,
                        isError = true,
                        dialogState = NotificationState.DialogState.Error(result.message)
                    )
                }
            }
        }
    }
}

data class NotificationState(

    val id: Long? = null,

    val notifications: List<Notification> = emptyList(),

    val unreadCount: Int = 0,

    val selectedNotification: Notification? = null,

    val selectedFilter: NotificationFilter = NotificationFilter.ALL,

    val uiState: ScreenUiState,

    val showOverlay: Boolean = false,

    val isError: Boolean = false,

    val dialogState: DialogState? = null

) {

    val filteredNotifications: List<Notification>
        get() = when (selectedFilter) {

            NotificationFilter.ALL -> notifications

            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
        }

    sealed interface DialogState {

        data class Error(val message: String) : DialogState
    }
}

enum class NotificationFilter {
    ALL,
    UNREAD
}

sealed interface NotificationEvent {

    data class NavigateToNotification(val notificationId: Long) : NotificationEvent

    data object NavigateBack : NotificationEvent

    data class ShowToast(val message: String) : NotificationEvent
}

sealed interface NotificationAction {

    data object LoadNotifications : NotificationAction

    data object LoadUnreadCount : NotificationAction

    data class FilterChanged(val filter: NotificationFilter) : NotificationAction

    data class NotificationClicked(val notification: Notification) : NotificationAction

    data class LoadNotification(val id: Long) : NotificationAction

    data object MarkAllAsReadClicked : NotificationAction

    data object ErrorDialogDismiss : NotificationAction

    sealed class Internal : NotificationAction {

        data class ReceiveNotificationsResult(val result: DataState<List<Notification>>) : Internal()

        data class ReceiveUnreadCountResult(val result: DataState<Int>) : Internal()

        data class ReceiveMarkAllAsReadResult(val result: DataState<String>) : Internal()

        data class ReceiveNotificationResult(val result: DataState<Notification>) : Internal()

        data class ReceiveMarkAsReadResult(val result: DataState<String>) : Internal()
    }
}

