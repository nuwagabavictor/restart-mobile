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
import androidx.compose.ui.text.font.FontWeight
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
import restart.shared.generated.resources.feature_register_app_logo_description
import restart.shared.generated.resources.feature_register_powered_by
import restart.shared.generated.resources.feature_sign_in_email_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    navigateToRegisterScreen: () -> Unit,
    navigateToCategoryScreen: () -> Unit,
    navigateToForgotPasswordScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->

        when (event) {

            LoginEvent.NavigateToSignup -> navigateToRegisterScreen()

            LoginEvent.NavigateToCategories -> navigateToCategoryScreen()

            LoginEvent.NavigateToForgotPassword -> navigateToForgotPasswordScreen()

            is LoginEvent.ShowToast ->
                scope.launch {
                    snackBarHostState.showSnackbar(event.message)
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
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    keyboardController?.hide()
                }
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(48.dp))

        LoginHeader()

        Spacer(Modifier.height(36.dp))

        LoginForm(
            state = state,
            onAction = onAction
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LoginHeader() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_logo),
            contentDescription = stringResource(
                Res.string.feature_register_app_logo_description
            ),
            modifier = Modifier
                .height(52.dp)
                .width(170.dp)
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(Res.string.feature_login),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.feature_sign_in_sub_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let {
                    Text(stringResource(it))
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
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let {
                    Text(stringResource(it))
                }
            },
            visualTransformation =
                if (state.isPasswordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {

                IconButton(
                    onClick = {
                        onAction(LoginAction.TogglePasswordVisibility)
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isPasswordVisible)
                                Res.drawable.ic_visibility
                            else
                                Res.drawable.ic_visibility_off
                        ),
                        contentDescription = null
                    )
                }
            }
        )

        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                onAction(LoginAction.NavigateToForgotPassword)
            }
        ) {
            Text(stringResource(Res.string.feature_sign_in_forgot_password))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = state.isLoginButtonEnabled && !state.showOverlay,
            shape = RoundedCornerShape(12.dp),
            onClick = {
                onAction(LoginAction.LoginClicked)
            }
        ) {

            if (state.showOverlay) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )

            } else {

                Text(stringResource(Res.string.feature_login))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(stringResource(Res.string.feature_sign_in_dont_have_an_account))

            TextButton(
                onClick = {
                    onAction(LoginAction.SignupClicked)
                }
            ) {
                Text(stringResource(Res.string.feature_sign_in_sign_up))
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                CircularProgressIndicator()

                Text("Signing you in...")
            }
        }
    }
}