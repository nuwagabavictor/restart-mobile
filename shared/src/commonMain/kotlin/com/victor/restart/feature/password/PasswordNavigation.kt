package com.victor.restart.feature.password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("change-password")
data object PasswordRoute

fun NavController.navigateToPassword(){
    navigate(PasswordRoute)
}

fun NavGraphBuilder.passwordDestination(
    navigateToCancel: () -> Unit
){
    composable<PasswordRoute>{
        PasswordScreen(
            navigateToCancel = navigateToCancel
        )
    }
}