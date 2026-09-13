package com.victor.restart.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("login")
data object LoginRoute

fun NavController.navigateToLoginScreen() {
    navigate(LoginRoute)
}

fun NavGraphBuilder.loginDestination(
    navigateToRegisterScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToForgotPasswordScreen: () -> Unit
) {

    composable<LoginRoute> {

        LoginScreen(
            navigateToRegisterScreen = navigateToRegisterScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToForgotPasswordScreen = navigateToForgotPasswordScreen
        )
    }
}