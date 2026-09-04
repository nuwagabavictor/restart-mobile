package com.victor.restart.feature.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("register")
data object RegisterRoute

fun NavController.navigateToRegisterScreen(){
    navigate(RegisterRoute)
}

fun NavGraphBuilder.registerDestination(
    navigateToLoginScreen: () -> Unit
){
    composable<RegisterRoute>{
        RegisterScreen(
            navigateToLoginScreen = navigateToLoginScreen
        )
    }
}
