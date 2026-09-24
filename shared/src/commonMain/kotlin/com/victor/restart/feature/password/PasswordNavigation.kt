package com.victor.restart.feature.password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.victor.restart.feature.settings.SettingsGraph
import com.victor.restart.feature.settings.SettingsRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("password_graph")
data object PasswordGraph
@Serializable
@SerialName("forgot-password")
data object ForgotPasswordRoute

@Serializable
@SerialName("change-password")
data object ChangePasswordRoute

fun NavController.navigateToChangePassword(){
    navigate(ChangePasswordRoute)
}

fun NavController.navigateToForgotPassword(){
    navigate(ForgotPasswordRoute)
}

fun NavGraphBuilder.passwordDestination(
    navController: NavController
){
    navigation<PasswordGraph>(
        startDestination = ForgotPasswordRoute
    ) {
        composable<ForgotPasswordRoute> {
            PasswordScreen(
                navigateToCancel = {
                    navController.popBackStack()
                }
            )
        }
        composable<ChangePasswordRoute> {
            PasswordScreen(
                navigateToCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}