package com.victor.restart.feature.password


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.enums.PasswordStrength
import com.victor.restart.core.utils.EventsEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_confirm
import restart.shared.generated.resources.feature_password_change_description
import restart.shared.generated.resources.feature_register_error_title
import restart.shared.generated.resources.feature_register_hide_password_description
import restart.shared.generated.resources.feature_register_ok
import restart.shared.generated.resources.feature_register_password_medium
import restart.shared.generated.resources.feature_register_password_strong
import restart.shared.generated.resources.feature_register_password_weak
import restart.shared.generated.resources.feature_register_show_password_description
import restart.shared.generated.resources.feature_sign_in_confirm_password_label
import restart.shared.generated.resources.feature_sign_in_password_label
import restart.shared.generated.resources.feature_sign_in_password_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    navigateToCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is PasswordEvent.NavigateToCancel -> {
                navigateToCancel()
            }

            is PasswordEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            Res.string.feature_sign_in_password_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateToCancel
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            PasswordScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = state,
                onAction = viewModel::trySendAction,
            )

            PasswordDialogs(
                dialogState = state.dialogState,
                onDismissRequest = {
                    viewModel.trySendAction(
                        PasswordAction.ErrorDialogDismiss
                    )
                }
            )

            if (state.showOverlay) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun PasswordDialogs(
    dialogState: PasswordState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {

        is PasswordState.DialogState.Error -> {
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
                        text = stringResource(
                            Res.string.feature_register_error_title
                        ),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = dialogState.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.feature_register_ok
                            )
                        )
                    }
                }
            }
        }

        null -> Unit
    }
}

@Composable
private fun PasswordScreenContent(
    modifier: Modifier = Modifier,
    state: PasswordState,
    onAction: (PasswordAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures {
                    keyboardController?.hide()
                }
            }
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        PasswordHeader()

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        InputBox(
            state = state,
            onAction = onAction,
        )

        Spacer(
            modifier = Modifier
                .height(24.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun PasswordHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = stringResource(
                Res.string.feature_sign_in_password_title
            ),
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(Res.string.feature_password_change_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputBox(
    modifier: Modifier = Modifier,
    state: PasswordState,
    onAction: (PasswordAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = {
                onAction(
                    PasswordAction.PasswordChanged(it)
                )
            },
            label = {
                Text(
                    text = stringResource(
                        Res.string.feature_sign_in_password_label
                    )
                )
            },
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let {
                    Text(
                        text = stringResource(it)
                    )
                } ?: Text(
                    text = when (state.isPasswordStrength) {
                        PasswordStrength.WEAK ->
                            stringResource(
                                Res.string.feature_register_password_weak
                            )

                        PasswordStrength.MEDIUM ->
                            stringResource(
                                Res.string.feature_register_password_medium
                            )

                        PasswordStrength.STRONG ->
                            stringResource(
                                Res.string.feature_register_password_strong
                            )
                    },
                    color = when (state.isPasswordStrength) {
                        PasswordStrength.WEAK ->
                            MaterialTheme.colorScheme.error

                        PasswordStrength.MEDIUM ->
                            MaterialTheme.colorScheme.tertiary

                        PasswordStrength.STRONG ->
                            MaterialTheme.colorScheme.primary
                    }
                )
            },
            visualTransformation =
                if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAction(
                            PasswordAction.TogglePasswordVisibility
                        )
                    }
                ) {
                    Icon(
                        imageVector =
                            if (state.isPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                        contentDescription = stringResource(
                            if (state.isPasswordVisible) {
                                Res.string.feature_register_hide_password_description
                            } else {
                                Res.string.feature_register_show_password_description
                            }
                        )
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.confirmPassword,
            onValueChange = {
                onAction(
                    PasswordAction.ConfirmPasswordChanged(it)
                )
            },
            label = {
                Text(
                    text = stringResource(
                        Res.string.feature_sign_in_confirm_password_label
                    )
                )
            },
            isError = state.confirmPasswordError != null,
            supportingText = {
                state.confirmPasswordError?.let {
                    Text(
                        text = stringResource(it)
                    )
                }
            },
            visualTransformation =
                if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAction(
                            PasswordAction.TogglePasswordVisibility
                        )
                    }
                ) {
                    Icon(
                        imageVector =
                            if (state.isPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                        contentDescription = stringResource(
                            if (state.isPasswordVisible) {
                                Res.string.feature_register_hide_password_description
                            } else {
                                Res.string.feature_register_show_password_description
                            }
                        )
                    )
                }
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isConfirmButtonEnabled,
            onClick = {
                onAction(
                    PasswordAction.ButtonClicked
                )
            }
        ) {
            Text(
                text = stringResource(
                    Res.string.feature_confirm
                )
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

