package com.victor.restart.feature.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_confirm
import restart.shared.generated.resources.feature_register_app_logo_description
import restart.shared.generated.resources.feature_register_error_title
import restart.shared.generated.resources.feature_register_hide_password_description
import restart.shared.generated.resources.feature_register_ok
import restart.shared.generated.resources.feature_register_password_medium
import restart.shared.generated.resources.feature_register_password_strong
import restart.shared.generated.resources.feature_register_password_weak
import restart.shared.generated.resources.feature_register_powered_by
import restart.shared.generated.resources.feature_register_show_password_description
import restart.shared.generated.resources.feature_sign_in_password_label
import restart.shared.generated.resources.feature_sign_in_sub_title
import restart.shared.generated.resources.feature_sign_in_title
import restart.shared.generated.resources.ic_logo
import restart.shared.generated.resources.ic_visibility
import restart.shared.generated.resources.ic_visibility_off
import com.victor.restart.core.enums.PasswordStrength
import com.victor.restart.core.utils.EventsEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.feature_sign_in_confirm_password_label
import restart.shared.generated.resources.feature_sign_in_password_title

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PasswordScreen(
    navigateToCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel = koinViewModel()
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow){
        event -> when(event){
            is PasswordEvent.NavigateToCancel -> navigateToCancel()
            is PasswordEvent.ShowToast -> {
                scope.launch { snackbarHostState.showSnackbar(event.message) }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(Res.string.feature_register_powered_by),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ){paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            PasswordDialogs(
                dialogState = state.dialogState,
                onDismissRequest = {
                    viewModel.trySendAction(PasswordAction.ErrorDialogDismiss)
                }
            )

            PasswordScreenContent(
                state = state,
                onAction = viewModel::trySendAction
            )

            if (state.showOverlay){
                LoadingOverlay()
            }
        }

    }
}

@Composable
private fun PasswordDialogs(
    dialogState: PasswordState.DialogState?,
    onDismissRequest: () -> Unit
){
    when(dialogState){
        is PasswordState.DialogState.Error -> {
            Dialog(
                onDismissRequest = onDismissRequest
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.feature_register_error_title),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = dialogState.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(Res.string.feature_register_ok))
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
    onAction: (PasswordAction) -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 100.dp, start = 16.dp, end = 16.dp)
            .pointerInput(Unit){
                detectTapGestures {
                    keyboardController?.hide()
                }
            }
            .verticalScroll(rememberScrollState())
    ){
        LogoBox()

        Spacer(modifier = Modifier.height(32.dp))

        InputBox(state=state, onAction = onAction)
    }
}

@Composable
private fun LogoBox(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier
                .height(48.dp)
                .width(165.dp),

            painter = painterResource(Res.drawable.ic_logo),
            contentDescription = stringResource(Res.string.feature_register_app_logo_description)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(stringResource(Res.string.feature_sign_in_password_title))

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InputBox(
    modifier: Modifier = Modifier,
    state: PasswordState,
    onAction: (PasswordAction) -> Unit
){
    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(12.dp)
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = {
                onAction(PasswordAction.PasswordChanged(it))
            },
            label = { Text(stringResource(Res.string.feature_sign_in_password_label)) },
            isError = state.passwordError != null,
            supportingText = { state.passwordError?.let { Text(stringResource(it)) }
                ?: Text(
                    text = when (state.isPasswordStrength) {
                        PasswordStrength.WEAK -> stringResource(Res.string.feature_register_password_weak)
                        PasswordStrength.MEDIUM -> stringResource(Res.string.feature_register_password_medium)
                        PasswordStrength.STRONG -> stringResource(Res.string.feature_register_password_strong)
                    },
                    color = when (state.isPasswordStrength) {
                        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
                        PasswordStrength.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        PasswordStrength.STRONG -> MaterialTheme.colorScheme.primary
                    }
                )

            },
            visualTransformation =
                if (state.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onAction(PasswordAction.TogglePasswordVisibility) }) {
                    Icon(
                        painter = painterResource(
                            if (state.isPasswordVisible) Res.drawable.ic_visibility else Res.drawable.ic_visibility_off
                        ),
                        contentDescription = stringResource(
                            if (state.isPasswordVisible) Res.string.feature_register_hide_password_description
                            else Res.string.feature_register_show_password_description
                        )
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.confirmPassword,
            onValueChange = {
                onAction(PasswordAction.ConfirmPasswordChanged(it))
            },
            label = { Text(stringResource(Res.string.feature_sign_in_confirm_password_label)) },
            isError = state.confirmPasswordError != null,
            supportingText = { state.confirmPasswordError?.let { Text(stringResource(it)) } },
            visualTransformation =
                if (state.isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onAction(PasswordAction.TogglePasswordVisibility) }) {
                    Icon(
                        painter = painterResource(
                            if (state.isPasswordVisible) Res.drawable.ic_visibility else Res.drawable.ic_visibility_off
                        ),
                        contentDescription = stringResource(
                            if (state.isPasswordVisible) Res.string.feature_register_hide_password_description
                            else Res.string.feature_register_show_password_description
                        )
                    )
                }
            }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isConfirmButtonEnabled,
            onClick = {onAction(PasswordAction.ButtonClicked)}
        ){
            Text(stringResource(Res.string.feature_confirm))
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Surface (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            CircularProgressIndicator()
        }
    }
}