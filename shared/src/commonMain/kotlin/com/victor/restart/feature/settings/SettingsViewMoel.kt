package com.victor.restart.feature.settings


import com.victor.restart.core.enums.LanguageConfig
import com.victor.restart.core.enums.ThemeConfig
import com.victor.restart.core.utils.ScreenUiState
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.utils.BaseViewModel

import kotlinx.coroutines.launch
import com.victor.restart.core.repository.userdata.UserDataRepository
import com.victor.restart.core.utils.DataState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import kotlinx.coroutines.flow.combine

class SettingsViewModel(
    private val userDataRepository: UserDataRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsAction>(
    initialState = SettingsState(uiState = ScreenUiState.Success,),
) {

    private var userDataJob: Job? = null
    private var preferencesJob: Job? = null

    private var logoutJob: Job? = null


    init {
        trySendAction(SettingsAction.Load)
    }

    private fun updateState(block: (SettingsState) -> SettingsState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: SettingsAction) {
        when (action) {

            SettingsAction.Load -> {
                loadUserData()
                loadPreferences()
            }

            SettingsAction.ProfileClicked -> {
                sendEvent(SettingsEvent.NavigateToProfile)
            }

            SettingsAction.NotificationsClicked -> {
                sendEvent(SettingsEvent.NavigateToNotifications)
            }

            SettingsAction.ChangeLanguageClicked -> {
                sendEvent(SettingsEvent.NavigateToLanguage)
            }

            SettingsAction.ChangeThemeClicked -> {
                sendEvent(SettingsEvent.NavigateToTheme)
            }

            SettingsAction.SupportClicked -> {
                sendEvent(SettingsEvent.NavigateToSupport)
            }

            SettingsAction.ChangePasswordClicked -> {
                sendEvent(SettingsEvent.NavigateToChangePassword)
            }

            SettingsAction.AboutClicked -> {
                sendEvent(SettingsEvent.NavigateToAbout)
            }

            SettingsAction.ErrorDismiss -> {
                updateState {
                    it.copy(
                        isError = false,
                        errorMessage = null,
                    )
                }
            }


            SettingsAction.LogOutClicked -> {
                updateState {
                    it.copy(
                        dialogState = SettingsState.DialogState.LogoutConfirmation
                    )
                }
            }

            SettingsAction.LogoutConfirmed -> {
                handleLogout()
            }
        }
    }

    private fun handleLogout(){
        logoutJob?.cancel()

        logoutJob = viewModelScope.launch {
            userPreferencesRepository.logOut()

            updateState {
                it.copy(dialogState = null)
            }

            sendEvent(SettingsEvent.Logout)
        }
    }

    private fun loadUserData() {
        userDataJob?.cancel()

        userDataJob = viewModelScope.launch {
            userDataRepository.userData.collect { result ->
                when (result) {

                    is DataState.Loading -> {
                        updateState {
                            it.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = null,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val user = result.data

                        updateState {
                            it.copy(
                                username = user.username,
                                email = user.email,
                                phone = user.phone,
                                initials = getInitials(user.username),
                                isLoading = false,
                                isError = false,
                                errorMessage = null,
                            )
                        }
                    }

                    is DataState.Error -> {
                        updateState {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = result.message,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadPreferences() {
        preferencesJob?.cancel()

        preferencesJob = viewModelScope.launch {
            combine(
                userPreferencesRepository.appTheme,
                userPreferencesRepository.observeLanguage,
            ) { theme, language ->
                theme to language
            }.collect { (theme, language) ->

                updateState {
                    it.copy(
                        theme = theme,
                        language = language,
                    )
                }
            }
        }
    }

    private fun getInitials(username: String): String {
        val nameParts = username
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        return when {
            nameParts.isEmpty() -> ""

            nameParts.size == 1 ->
                nameParts.first()
                    .take(2)
                    .uppercase()

            else ->
                "${nameParts.first().first()}${nameParts.last().first()}"
                    .uppercase()
        }
    }
}

data class SettingsState(
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val initials: String = "",

    val theme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val language: LanguageConfig = LanguageConfig.DEFAULT,

    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,

    val uiState: ScreenUiState,

    val showOverlay: Boolean = false,

    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String, ) : DialogState
        data object LogoutConfirmation : DialogState

    }


}

sealed interface SettingsAction {

    data object Load : SettingsAction

    data object ProfileClicked : SettingsAction

    data object NotificationsClicked : SettingsAction

    data object ChangeLanguageClicked : SettingsAction

    data object ChangeThemeClicked : SettingsAction

    data object SupportClicked : SettingsAction

    data object LogOutClicked: SettingsAction

    data object LogoutConfirmed : SettingsAction

    data object ChangePasswordClicked : SettingsAction

    data object AboutClicked : SettingsAction

    data object ErrorDismiss : SettingsAction
}

sealed interface SettingsEvent {

    data object NavigateToProfile : SettingsEvent

    data object NavigateToNotifications : SettingsEvent

    data object NavigateToLanguage : SettingsEvent

    data object NavigateToTheme : SettingsEvent

    data object NavigateToSupport : SettingsEvent

    data object NavigateToChangePassword : SettingsEvent

    data object NavigateToAbout : SettingsEvent

    data object Logout : SettingsEvent

    data class ShowToast(val message: String, ) : SettingsEvent
}