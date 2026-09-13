package com.victor.restart.feature.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.entity.User
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.repository.user.UserRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import com.victor.restart.core.utils.UserData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_sign_in_email_error
import restart.shared.generated.resources.feature_sign_in_password_error

class LoginViewModel(
    private val userRepository: UserRepository,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = LoginState(uiState = ScreenUiState.Success)
) {

    private var loginJob: Job? = null

    init {
        savedStateHandle.get<String>("email")?.let {
            trySendAction(LoginAction.EmailChanged(it))
        }
    }

    private fun updateState(block: (LoginState) -> LoginState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: LoginAction) {

        when (action) {

            is LoginAction.EmailChanged ->
                updateState {
                    it.copy(
                        email = action.email,
                        emailError = null,
                        isError = false
                    )
                }

            is LoginAction.PasswordChanged ->
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        isError = false
                    )
                }

            LoginAction.TogglePasswordVisibility ->
                updateState {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }

            LoginAction.LoginClicked -> login()

            LoginAction.SignupClicked -> sendEvent(LoginEvent.NavigateToSignup)

            LoginAction.NavigateToForgotPassword -> sendEvent(LoginEvent.NavigateToForgotPassword)

            LoginAction.ErrorDialogDismiss ->
                updateState {
                    it.copy(dialogState = null)
                }

            is LoginAction.Internal.ReceiveLoginResult -> handleLoginResult(action.loginResult)
        }
    }

    //Login
    private fun login() {

        if (!validate()) return

        loginJob?.cancel()

        updateState {
            it.copy(showOverlay = true)
        }

        loginJob = viewModelScope.launch {

            val result = userRepository.login(state.email.trim(), state.password)

            sendAction(LoginAction.Internal.ReceiveLoginResult(result))
        }
    }

    private fun handleLoginResult(result: DataState<User>) {
        viewModelScope.launch {
            when (result) {

                is DataState.Loading ->
                    updateState {
                        it.copy(showOverlay = true)
                    }

                is DataState.Success -> {

                    val user = result.data

                    preferencesRepository.updateUser(
                        UserData(
                            id = user.id,
                            username = user.username.orEmpty(),
                            email = user.email,
                            role = user.role.orEmpty(),
                            accessToken = user.accessToken,
                            isAuthenticated = true
                        )
                    )

                    updateState {
                        it.copy(
                            showOverlay = false,
                            isError = false
                        )
                    }

                    sendEvent(LoginEvent.ShowToast("Welcome ${user.username.orEmpty()}"))

                    //sendEvent(LoginEvent.NavigateToCategories)
                }

                is DataState.Error -> {

                    updateState {
                        it.copy(
                            showOverlay = false,
                            isError = true,
                            emailError = Res.string.feature_sign_in_email_error,
                            passwordError = Res.string.feature_sign_in_password_error,
                            dialogState = LoginState.DialogState.Error(result.message)
                        )
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {

        val emailError: StringResource? =
            if (state.email.isBlank()) {
                Res.string.feature_sign_in_email_error
            } else null

        val passwordError: StringResource? =
            if (state.password.length < 8) {
                Res.string.feature_sign_in_password_error
            } else null

        val hasError = emailError != null || passwordError != null

        updateState {
            it.copy(
                isError = hasError,
                emailError = emailError,
                passwordError = passwordError
            )
        }

        return !hasError
    }
}

data class LoginState(
    val email: String ="",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
    val emailError: StringResource? = null,
    val passwordError: StringResource? = null,
    val dialogState: DialogState? = null,
    val uiState: ScreenUiState?,
    val showOverlay: Boolean = false,
    ){
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }

    val isLoginButtonEnabled: Boolean
        get() = email.isNotEmpty() && password.length >=8;
}

sealed interface LoginEvent {
    data object NavigateToSignup : LoginEvent
    data object NavigateToHome : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
    data class ShowToast(val message: String) : LoginEvent
}

sealed interface LoginAction {
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data object TogglePasswordVisibility : LoginAction

    data object ErrorDialogDismiss : LoginAction
    data object LoginClicked : LoginAction
    data object SignupClicked : LoginAction
    data object NavigateToForgotPassword : LoginAction

    sealed class Internal : LoginAction {
        data class ReceiveLoginResult(val loginResult: DataState<User>) : Internal()
    }
}