package com.victor.restart.feature.home


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.nav_categories
import restart.shared.generated.resources.nav_home
import restart.shared.generated.resources.nav_settings
import restart.shared.generated.resources.nav_transactions

sealed class BottomNavItem(
    val icon: ImageVector,
    val label: StringResource,
    val routeKey: String
) {
    data object Home : BottomNavItem(
       icon = Icons.Default.Home,
        label = Res.string.nav_home,
        routeKey = "home"
    )
    data object Categories : BottomNavItem(
        icon = Icons.Default.Category,
        label = Res.string.nav_categories,
        routeKey = "category"
    )
    data object Transactions : BottomNavItem(
        icon = Icons.Default.AccountBalanceWallet,
        label = Res.string.nav_transactions,
        routeKey = "transaction"
    )

    data object Settings : BottomNavItem(
        icon = Icons.Default.Settings,
        label = Res.string.nav_settings,
        routeKey = "settings"
    )

    companion object {
        val items = listOf(Home, Categories, Transactions, Settings)
    }
}