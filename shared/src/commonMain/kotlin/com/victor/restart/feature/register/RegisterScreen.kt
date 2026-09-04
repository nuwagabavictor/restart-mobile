package com.victor.restart.feature.register

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_register_app_logo_description
import restart.shared.generated.resources.feature_register_error_title
import restart.shared.generated.resources.feature_register_hide_password_description
import restart.shared.generated.resources.feature_register_ok
import restart.shared.generated.resources.feature_register_password_medium
import restart.shared.generated.resources.feature_register_password_strong
import restart.shared.generated.resources.feature_register_password_weak
import restart.shared.generated.resources.feature_register_powered_by
import restart.shared.generated.resources.feature_register_show_password_description
import restart.shared.generated.resources.feature_sign_in_email_label
import restart.shared.generated.resources.feature_sign_in_password_label
import restart.shared.generated.resources.feature_sign_in_phone_label
import restart.shared.generated.resources.feature_sign_in_sign_in
import restart.shared.generated.resources.feature_sign_in_sign_up
import restart.shared.generated.resources.feature_sign_in_sub_title
import restart.shared.generated.resources.feature_sign_in_title
import restart.shared.generated.resources.feature_sign_in_username_label
import restart.shared.generated.resources.ic_logo
import restart.shared.generated.resources.ic_visibility
import restart.shared.generated.resources.ic_visibility_off
import com.victor.restart.core.enums.PasswordStrength
import com.victor.restart.core.utils.EventsEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = koinViewModel ()
){
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember{ SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow){event ->
        when(event){
            is RegisterEvent.NavigateToLogin -> navigateToLoginScreen()
            is RegisterEvent.ShowToast -> {
                scope.launch { snackBarHostState.showSnackbar(event.message) }
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
    ){paddingValues ->
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ){
            RegisterDialogs(
                dialogState = state.dialogState,
                onDismissRequest = {
                    viewModel.trySendAction(RegisterAction.ErrorDialogDismiss)
                }
            )

            RegisterScreenContent(
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
private fun RegisterDialogs(
    dialogState: RegisterState.DialogState?,
    onDismissRequest: () -> Unit
){
   when(dialogState){
       is RegisterState.DialogState.Error -> {
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
                   verticalArrangement = Arrangement.spacedBy(16.dp)
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
private fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    onAction: (RegisterAction) -> Unit,
    state: RegisterState
){
    val keyboardController = LocalSoftwareKeyboardController.current;
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

        InputBox(state= state, onAction=onAction)
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
            contentDescription = stringResource(Res.string.feature_register_app_logo_description)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(stringResource(Res.string.feature_sign_in_title))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(Res.string.feature_sign_in_sub_title))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputBox(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.username,
            onValueChange = { onAction(RegisterAction.UserNameChanged(it)) },
            label = { Text(stringResource(Res.string.feature_sign_in_username_label)) },
            isError = state.userNameError != null,
            supportingText = { state.userNameError?.let { Text(stringResource(it)) } }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            onValueChange = { onAction(RegisterAction.EmailChanged(it)) },
            label = { Text(stringResource(Res.string.feature_sign_in_email_label)) },
            isError = state.emailError != null,
            supportingText = { state.emailError?.let { Text(stringResource(it)) } }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.phone,
            onValueChange = { onAction(RegisterAction.PhoneChanged(it)) },
            label = { Text(stringResource(Res.string.feature_sign_in_phone_label)) },
            isError = state.phoneError != null,
            supportingText = { state.phoneError?.let { Text(stringResource(it)) } }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            onValueChange = { onAction(RegisterAction.PasswordChanged(it)) },
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
                IconButton(onClick = { onAction(RegisterAction.TogglePasswordVisibility) }) {
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
            enabled = state.isRegisterButtonEnabled,
            onClick = {onAction(RegisterAction.RegisterClicked)}
        ){
            Text(stringResource(Res.string.feature_sign_in_sign_up))
        }
        TextButton(onClick = {onAction(RegisterAction.LoginClicked)}){
            Text(stringResource(Res.string.feature_sign_in_sign_in))
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