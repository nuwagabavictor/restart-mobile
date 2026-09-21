package com.victor.restart.feature.settings


import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.victor.restart.feature.language.ChangeLanguageScreen
import com.victor.restart.feature.notification.navigateToNotifications
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("settings_graph")
data object SettingsGraph

@Serializable
@SerialName("settings")
data object SettingsRoute

@Serializable
@SerialName("change_language")
data object ChangeLanguageRoute

fun NavController.navigateToChangeLanguage() {
    navigate(ChangeLanguageRoute)
}

fun NavController.navigateToSettings() {
    navigate(SettingsRoute) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.settingsDestination(
    navController: NavController
) {
    navigation<SettingsGraph>(
        startDestination = SettingsRoute
    ) {

        composable<SettingsRoute> {
            SettingsScreen(
                onProfileClick = {
                    // Will be connected later
                },
                onNotificationsClick = {
                    navController.navigateToNotifications()
                },
                onChangeLanguageClick = {
                    navController.navigateToChangeLanguage()
                },
                onSupportClick = {
                    // Will be connected later
                },
                onChangePasswordClick = {
                    // Will be connected later
                },
                onAboutClick = {
                    // Will be connected later
                }
            )
        }

        composable<ChangeLanguageRoute> {
            ChangeLanguageScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}