package com.victor.restart.feature.password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_sign_in_password_error
import restart.shared.generated.resources.internal_server_error
import com.victor.restart.core.enums.PasswordStrength
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.repository.user.UserRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


class PasswordViewModel(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<PasswordState, PasswordEvent, PasswordAction>(
    initialState = PasswordState(
        uiState = ScreenUiState.Success
    )
) {

    private var passwordJob: Job? = null

    init {
        savedStateHandle.get<String>("password")?.let {
            trySendAction(
                PasswordAction.PasswordChanged(it)
            )
        }
    }

    private fun updateState(
        state: (PasswordState) -> PasswordState
    ) {
        mutableStateFlow.update(state)
    }

    override fun handleAction(action: PasswordAction) {
        when (action) {

            is PasswordAction.PasswordChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        password = action.password,
                        passwordError = null,
                        isPasswordStrength =
                            PasswordStrength.calculatePasswordStrength(
                                action.password
                            )
                    )
                }
            }

            is PasswordAction.ConfirmPasswordChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }

            is PasswordAction.TogglePasswordVisibility -> {
                updateState {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            is PasswordAction.ButtonClicked -> {
                changePassword()
            }

            is PasswordAction.ErrorDialogDismiss -> {
                updateState {
                    it.copy(
                        dialogState = null
                    )
                }
            }

            is PasswordAction.Internal.ReceivePasswordAction -> {
                handlePasswordResult(action)
            }

            is PasswordAction.CancelClicked -> {
                sendEvent(
                    PasswordEvent.NavigateToCancel
                )
            }
        }
    }

    private fun handlePasswordResult(
        action: PasswordAction.Internal.ReceivePasswordAction
    ) {
        viewModelScope.launch {
            when (action.passwordResult) {

                is DataState.Error -> {

                    val errMsg =
                        if (
                            action.passwordResult.exception.cause
                                    is ServerResponseException
                        ) {
                            getString(
                                Res.string.internal_server_error
                            )
                        } else {
                            action.passwordResult.message
                        }

                    updateState {
                        it.copy(
                            isError = true,
                            uiState = ScreenUiState.Success,
                            showOverlay = false,
                            dialogState = PasswordState.DialogState.Error(
                                errMsg
                            ),
                            passwordError =
                                Res.string.feature_sign_in_password_error,
                            confirmPasswordError =
                                Res.string.feature_sign_in_password_error
                        )
                    }
                }

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
                            isError = false
                        )
                    }

                    // Password changed successfully.
                    // Immediately terminate the current session.
                    userPreferencesRepository.logOut()
                }
            }
        }
    }

    private fun changePassword() {
        passwordJob?.cancel()

        updateState {
            it.copy(
                showOverlay = true
            )
        }

        passwordJob = viewModelScope.launch {
            delay(300)

            val result = userRepository.changePassword(
                state.password,
                state.confirmPassword
            )

            sendAction(PasswordAction.Internal.ReceivePasswordAction(result))
        }
    }
}

data class PasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val uiState: ScreenUiState,
    val isPasswordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
    val showOverlay: Boolean = false,
    val passwordError: StringResource? = null,
    val confirmPasswordError: StringResource? = null,
    val dialogState: DialogState? = null
){
    sealed interface DialogState{
        data class Error(val message: String): DialogState
    }

    private val isPasswordNotEmpty: Boolean
        get() = password.isNotBlank()

    private val isConfirmPasswordNotEmpty: Boolean
        get() = confirmPassword.isNotBlank()

    private val isPasswordValid: Boolean
        get() = password.length >= 8 && isPasswordStrength != PasswordStrength.WEAK

    private val isPasswordMatching: Boolean
        get() = password == confirmPassword

    val isConfirmButtonEnabled: Boolean
        get() = isPasswordNotEmpty && isConfirmPasswordNotEmpty && isPasswordValid && isPasswordMatching
}

sealed interface PasswordEvent{
    data object NavigateToCancel: PasswordEvent
    data class ShowToast(val message: String): PasswordEvent
}

sealed interface PasswordAction{
    data class PasswordChanged(val password: String): PasswordAction
    data class ConfirmPasswordChanged(val confirmPassword: String): PasswordAction
    data object TogglePasswordVisibility: PasswordAction
    data object ButtonClicked: PasswordAction
    data object CancelClicked: PasswordAction
    data object ErrorDialogDismiss: PasswordAction

    sealed class Internal: PasswordAction{
        data class ReceivePasswordAction(val passwordResult: DataState<String>) : Internal()
    }
}