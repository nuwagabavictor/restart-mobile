package com.victor.restart



import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.victor.restart.feature.login.loginDestination
import com.victor.restart.feature.login.navigateToLoginScreen
import com.victor.restart.feature.password.navigateToPassword
import com.victor.restart.feature.password.passwordDestination
import com.victor.restart.feature.register.RegisterRoute
import com.victor.restart.feature.register.navigateToRegisterScreen
import com.victor.restart.feature.register.registerDestination

/**
 * Single shared entry point for the whole app's UI. Both MainActivity (Android)
 * and MainViewController (iOS) call this — it's the KMP equivalent of a shared
 * "root" servlet/controller that every platform-specific launcher delegates to.
 */
@Composable
fun App() {
    MaterialTheme {
        Surface {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = RegisterRoute,
            ) {
                registerDestination(
                    navigateToLoginScreen = {
                      navController.navigateToLoginScreen()
                    },
                )
                loginDestination(
                    navigateToRegisterScreen = {
                        navController.navigateToRegisterScreen()
                    },
                    navigateToPasscodeScreen = {},
                    navigateToForgotPasswordScreen = {
                        navController.navigateToPassword()
                    }
                )
                passwordDestination  (
                    navigateToCancel = {
                        navController.navigateToPassword()
                    }
                )
            }
        }
    }
}