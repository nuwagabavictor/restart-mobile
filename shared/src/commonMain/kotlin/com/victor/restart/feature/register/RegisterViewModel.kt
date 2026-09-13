package com.victor.restart.feature.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_sign_in_email_error
import restart.shared.generated.resources.feature_sign_in_password_error
import restart.shared.generated.resources.feature_sign_in_phone_error
import restart.shared.generated.resources.feature_sign_in_username_error
import restart.shared.generated.resources.internal_server_error
import com.victor.restart.core.entity.RegisterPayload
import com.victor.restart.core.enums.PasswordStrength
import com.victor.restart.core.repository.user.UserRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.GlobalConstants.EMAIL_REGEX
import com.victor.restart.core.utils.NetworkMonitor
import com.victor.restart.core.utils.ScreenUiState
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Suppress("TooManyFunctions")
class RegisterViewModel (
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle
): BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(
    initialState = RegisterState(uiState = ScreenUiState.Success)
){
    private var registerJob: Job? = null

    init {
        observeNetwork()

        savedStateHandle.get<String>("username")?.let {
            trySendAction(RegisterAction.UserNameChanged(it))
        }
    }


    /** Observes the network connectivity status and updates state accordingly. */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    sendAction(RegisterAction.ReceiveNetworkStatus(isOnline))
                }
        }
    }

    private fun updateState(state: (RegisterState) -> RegisterState){
        mutableStateFlow.update (state)
    }

    override fun handleAction(action: RegisterAction) {
        when(action){
            is RegisterAction.UserNameChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        username = action.username,
                        userNameError = null
                    )
                }
            }

            is RegisterAction.EmailChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        email = action.email,
                        emailError = null
                    )
                }
            }
            is RegisterAction.PhoneChanged -> {
                updateState { it.copy(
                    isError = false,
                    phone = action.phone,
                    phoneError = null
                ) }
            }
            is RegisterAction.RoleChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        role = action.role,
                        roleError = null
                    )
                }
            }
            is RegisterAction.PasswordChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        password = action.password,
                        passwordError = null,
                        isPasswordStrength = PasswordStrength.calculatePasswordStrength(action.password)
                    )
                }
            }
            is RegisterAction.TogglePasswordVisibility -> {
                updateState {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
            is RegisterAction.RegisterClicked -> register()
            is RegisterAction.LoginClicked -> sendEvent(RegisterEvent.NavigateToLogin)
            is RegisterAction.Internal.ReceiveRegisterResult -> handleRegisterResult(action.registerResult)
            is RegisterAction.ErrorDialogDismiss -> {
                updateState { it.copy(dialogState = null) }
            }

            is RegisterAction.ReceiveNetworkStatus -> handleNetworkStatus(action.isOnline)

        }
    }

    private fun handleRegisterResult(action: DataState<String>){
        viewModelScope.launch {
            when(action){
                is DataState.Error ->{
                    val errMsg =
                        if (action.exception.cause is ServerResponseException){
                            getString(Res.string.internal_server_error)
                        }else{
                            action.message
                        }
                    updateState {
                        it.copy(
                            isError = true,
                            uiState = ScreenUiState.Success,
                            dialogState = RegisterState.DialogState.Error(errMsg),
                            showOverlay = false,
                            userNameError = Res.string.feature_sign_in_username_error,
                            passwordError = Res.string.feature_sign_in_username_error,
                            roleError = Res.string.feature_sign_in_username_error,
                            phoneError = Res.string.feature_sign_in_username_error,
                            emailError = Res.string.feature_sign_in_username_error
                        )
                    }
                }
                is DataState.Loading -> {
                    updateState {
                        it.copy(showOverlay = true)
                    }
                }

                is DataState.Success -> {
                    updateState {
                        it.copy(showOverlay = false)
                    }
                    val registerData = action.data
                    updateState {
                        it.copy(
                            isError = false
                        )
                    }

                    sendEvent(RegisterEvent.NavigateToLogin)
                }
            }
        }
    }

    /**
     * Handles changes in network connectivity.
     *
     * It updates the `networkStatus` state. If the network is offline, it sets the
     * `uiState` to [ScreenUiState.Network]. If the network is online, it
     * automatically triggers a data fetch to refresh the content.
     *
     * @param isOnline A boolean indicating the current network status.
     */
    private fun handleNetworkStatus(isOnline: Boolean) {
        updateState { it.copy(networkStatus = isOnline) }

        viewModelScope.launch {
            if (!isOnline) {
                updateState { current ->
                    if (current.uiState is ScreenUiState.Loading ||
                        current.uiState is ScreenUiState.Error ||
                        current.uiState is ScreenUiState.Empty ||
                        current.uiState is ScreenUiState.Network
                    ) {
                        current.copy(uiState = ScreenUiState.Network)
                    } else {
                        current
                    }
                }
            } else {
                //sendAction(RegisterAction.)
            }
        }
    }

    private fun register(){
        if (!validateAndShowErrors()) return
        registerJob?.cancel()

        updateState { it.copy(showOverlay = true) }

        registerJob = viewModelScope.launch {
            delay(300)
            val result = userRepository.createUser(
                payload = RegisterPayload(
                    username = state.username,
                    phone = state.phone,
                    email = state.email,
                    role = state.role.ifBlank { "USER" } ?: "USER",
                    password = state.password

            ));
            sendAction(RegisterAction.Internal.ReceiveRegisterResult(result))
        }
    }

    private fun validateAndShowErrors(): Boolean {
        val usernameError = if (state.username.isBlank()) Res.string.feature_sign_in_username_error else null
        val emailError = if (state.email.isBlank() || !EMAIL_REGEX.matches(state.email))
            Res.string.feature_sign_in_email_error else null
        val phoneError = if (state.phone.isBlank()) Res.string.feature_sign_in_phone_error else null
        val passwordError = if (state.password.length < 8 || state.isPasswordStrength == PasswordStrength.WEAK)
            Res.string.feature_sign_in_password_error else null

        val hasError = listOf(usernameError, emailError, phoneError, passwordError).any { it != null }

        if (hasError) {
            updateState {
                it.copy(
                    isError = true,
                    userNameError = usernameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passwordError = passwordError
                )
            }
        }

        return !hasError
    }

}

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String = "",
    val password: String = "",
    val isPasswordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
    val userNameError: StringResource? = null,
    val emailError: StringResource? = null,
    val passwordError: StringResource? = null,
    val phoneError: StringResource? = null,
    val roleError: StringResource? = null,
    val uiState: ScreenUiState,
    val showOverlay : Boolean = false,
    val dialogState: DialogState? = null,
    val networkStatus: Boolean = false,


    ){
    sealed interface DialogState{
        data class Error(val message: String) : DialogState
    }

    private val isUsernameValid: Boolean
        get() = username.isNotBlank()

    private val isEmailValid: Boolean
        get() = email.isNotBlank() && EMAIL_REGEX.matches(email)

    private val isPhoneValid: Boolean
        get() = phone.isNotBlank() && phone.length >= 7

    private val isPasswordValid: Boolean
        get() = password.length >= 8 && isPasswordStrength != PasswordStrength.WEAK

    val isRegisterButtonEnabled: Boolean
        get() = isUsernameValid && isEmailValid && isPhoneValid && isPasswordValid

}

sealed interface RegisterEvent{
    data object NavigateToLogin: RegisterEvent

    data class ShowToast(val message: String): RegisterEvent
}

sealed interface RegisterAction{
    data object ErrorDialogDismiss : RegisterAction

    data class UserNameChanged(val username: String): RegisterAction
    data class PasswordChanged(val password: String): RegisterAction
    data class EmailChanged(val email: String): RegisterAction
    data class RoleChanged(val role: String): RegisterAction
    data class PhoneChanged(val phone: String): RegisterAction

    data class ReceiveNetworkStatus(val isOnline: Boolean) : RegisterAction


    data object TogglePasswordVisibility: RegisterAction
    data object LoginClicked: RegisterAction
    data object RegisterClicked: RegisterAction

    sealed class Internal: RegisterAction{
        data class ReceiveRegisterResult(
            val registerResult: DataState<String>
        ) : Internal()
    }
}

