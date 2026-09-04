package com.victor.restart.feature.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_sign_in_password_error
import restart.shared.generated.resources.internal_server_error
import restart.shared.generated.resources.no_client_assigned
import com.victor.restart.core.entity.User
import com.victor.restart.core.repository.UserPreferencesRepository
import com.victor.restart.core.repository.UserRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import com.victor.restart.core.utils.UserData
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import restart.shared.generated.resources.feature_sign_in_email_error

class LoginViewModel(
    private val userRepository: UserRepository,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = LoginState(uiState= ScreenUiState.Success)
){
    private var loginJob: Job? = null

    init {
        savedStateHandle.get<String>("email")?.let {
            trySendAction(LoginAction.EmailChanged(it))
        }
    }
    private fun updateState(update: (LoginState) -> LoginState){
        mutableStateFlow.update(update)
    }

    override fun handleAction(action: LoginAction) {
        when (action){
            is LoginAction.EmailChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        email = action.email,
                        emailError = null
                    )
                }
            }
            is LoginAction.PasswordChanged -> {
                updateState {
                    it.copy(
                        isError = false,
                        password = action.password,
                        passwordError = null
                    )
                }
            }
            is LoginAction.TogglePasswordVisibility -> {
                updateState {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
            is LoginAction.LoginClicked -> login(state.email, state.password)
            is LoginAction.SignupClicked -> sendEvent(LoginEvent.NavigateToSignup)
            is LoginAction.NavigateToForgotPassword -> sendEvent(LoginEvent.NavigateToForgotPassword)
            is LoginAction.Internal.ReceiveLoginResult -> handleLoginResult(action)
            is LoginAction.ErrorDialogDismiss -> {
                updateState {
                    it.copy(dialogState = null)
                }
            }
        }
    }


    private fun handleLoginResult(action: LoginAction.Internal.ReceiveLoginResult) {
        viewModelScope.launch {
            when (action.loginResult) {
                is DataState.Error -> {
                    val errorMsg =
                        if (action.loginResult.exception.cause is ServerResponseException) {
                            getString(
                                Res.string.internal_server_error,
                            )
                        } else {
                            action.loginResult.message
                        }

                    updateState {
                        it.copy(
                            isError = true,
                            uiState = ScreenUiState.Success,
                            showOverlay = false,
                            dialogState = LoginState.DialogState.Error(errorMsg),
                            emailError = Res.string.feature_sign_in_email_error,
                            passwordError = Res.string.feature_sign_in_password_error,
                        )
                    }
                }

                is DataState.Loading -> {
                    updateState { it.copy(showOverlay = true) }
                }

                is DataState.Success -> {
                    updateState { it.copy(showOverlay = false) }
                    val user = action.loginResult.data
                    if (user.email.isEmpty()) {
                        val noClientsMsg = getString(Res.string.no_client_assigned)
                        viewModelScope.launch {
                           preferencesRepository.updateUser(
                               UserData(
                                   id = user.id,
                                   email = user.email,
                                   username = user.username ?: "",
                                   role = user.role,
                                   accessToken = user.accessToken,
                                   isAuthenticated = true
                               )
                           )
                        }
                        updateState {
                            it.copy(
                                isError = true,
                                dialogState = LoginState.DialogState.Error(noClientsMsg),
                            )
                        }
                    } else {
                        val userData = UserData(
                            id = user.id,
                            username = user.username.orEmpty(),
                            email = user.email,
                            role = user.role.orEmpty(),
                            accessToken = user.accessToken,
                            isAuthenticated = true
                        )
                        viewModelScope.launch {
                            preferencesRepository.updateUser(userData)
                        }
                        sendEvent(LoginEvent.NavigateToPasscode)
                    }
                }
            }
        }
    }



    private fun login(email: String, password: String){
        loginJob?.cancel()
        updateState { it.copy(showOverlay = true) }

        loginJob = viewModelScope.launch {
            delay(300);
            val result = userRepository.login(email, password);
            println("Access Token: ${result.data?.accessToken}")
            //Log.d("LOGIN", "Access Token: ${result.data?.accessToken}")
            sendAction(LoginAction.Internal.ReceiveLoginResult(result))
        }
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
    data object NavigateToPasscode : LoginEvent
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
        data class ReceiveLoginResult(
            val loginResult: DataState<User>,
        ) : Internal()
    }
}