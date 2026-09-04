package com.victor.restart.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_sign_in_dont_have_an_account
import restart.shared.generated.resources.feature_sign_in_forgot_password
import restart.shared.generated.resources.feature_sign_in_password_label
import restart.shared.generated.resources.feature_sign_in_sign_up
import restart.shared.generated.resources.feature_sign_in_sub_title
import restart.shared.generated.resources.feature_sign_in_title

// ---- ADD YOUR DRAWABLE RESOURCES HERE ----
import restart.shared.generated.resources.ic_logo
import restart.shared.generated.resources.ic_error
import restart.shared.generated.resources.ic_visibility
import restart.shared.generated.resources.ic_visibility_off

import com.victor.restart.core.utils.EventsEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.feature_login
import restart.shared.generated.resources.feature_register_powered_by
import restart.shared.generated.resources.feature_sign_in_email_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    navigateToRegisterScreen: () -> Unit,
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToPasscodeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoginEvent.NavigateToSignup -> navigateToRegisterScreen()
            LoginEvent.NavigateToPasscode -> navigateToPasscodeScreen()
            LoginEvent.NavigateToForgotPassword -> navigateToForgotPasswordScreen()

            is LoginEvent.ShowToast -> {
                scope.launch {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }


    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(Res.string.feature_register_powered_by),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LoginDialogs(
                dialogState = state.dialogState,
                onDismissRequest = {
                    viewModel.trySendAction(LoginAction.ErrorDialogDismiss)
                }
            )

            LoginScreenContent(
                state = state,
                onAction = viewModel::trySendAction
            )

            if (state.showOverlay) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun LoginDialogs(
    dialogState: LoginState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is LoginState.DialogState.Error -> {
            Dialog(
                onDismissRequest = onDismissRequest
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = dialogState.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("OK")
                    }
                }
            }
        }

        null -> Unit
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    modifier: Modifier = Modifier,
    onAction: (LoginAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 16.dp, end = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    keyboardController?.hide()
                }
            }
            .verticalScroll(rememberScrollState())
    ) {
        LogoBox()

        Spacer(modifier = Modifier.height(32.dp))

        InputBox(
            state = state,
            onAction = onAction
        )
    }
}

@Composable
private fun LogoBox(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Image(
            modifier = Modifier
                .height(48.dp)
                .width(165.dp),

            painter = painterResource(Res.drawable.ic_logo),
            contentDescription = "Application Logo"
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = stringResource(Res.string.feature_login),
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputBox(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = {
                onAction(LoginAction.EmailChanged(it))
            },
            label = {
                Text(stringResource(Res.string.feature_sign_in_email_label))
            },
            isError = state.isError,
            supportingText = {
                if (state.isError) {
                    state.emailError?.let {
                        Text(stringResource(it))
                    }
                }
            },
            trailingIcon = {
                if (state.isError) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_error),
                        contentDescription = "Error"
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = {
                onAction(LoginAction.PasswordChanged(it))
            },
            label = {
                Text(stringResource(Res.string.feature_sign_in_password_label))
            },
            isError = state.isError,
            supportingText = {
                if (state.isError) {
                    state.passwordError?.let {
                        Text(stringResource(it))
                    }
                }
            },
            visualTransformation =
                if (state.isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {
                IconButton(
                    onClick = {
                        onAction(LoginAction.TogglePasswordVisibility)
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isPasswordVisible)
                                Res.drawable.ic_visibility_off
                            else
                                Res.drawable.ic_visibility
                        ),
                        contentDescription =
                            if (state.isPasswordVisible)
                                "Hide password"
                            else
                                "Show password"
                    )
                }
            }
        )

        TextButton(onClick = {
            onAction(LoginAction.NavigateToForgotPassword)},
            modifier = Modifier
                .align(Alignment.End)
        ) {
            Text(stringResource(Res.string.feature_sign_in_forgot_password))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = state.isLoginButtonEnabled,
            onClick = {
                onAction(LoginAction.LoginClicked)
            }
        ) {
            Text(stringResource(Res.string.feature_login))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.feature_sign_in_dont_have_an_account))

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = { onAction(LoginAction.SignupClicked) }) {
                Text(stringResource(Res.string.feature_sign_in_sign_up))
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}