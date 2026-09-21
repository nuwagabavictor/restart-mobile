package com.victor.restart.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.enums.ThemeConfig
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_cancel
import restart.shared.generated.resources.feature_settings_about
import restart.shared.generated.resources.feature_settings_about_us
import restart.shared.generated.resources.feature_settings_account
import restart.shared.generated.resources.feature_settings_change_language
import restart.shared.generated.resources.feature_settings_change_password
import restart.shared.generated.resources.feature_settings_change_theme
import restart.shared.generated.resources.feature_settings_contact_support
import restart.shared.generated.resources.feature_settings_get_in_touch
import restart.shared.generated.resources.feature_settings_learn_more
import restart.shared.generated.resources.feature_settings_logout
import restart.shared.generated.resources.feature_settings_logout_confirmation
import restart.shared.generated.resources.feature_settings_logout_subtitle
import restart.shared.generated.resources.feature_settings_notifications
import restart.shared.generated.resources.feature_settings_notifications_subtitle
import restart.shared.generated.resources.feature_settings_preferences
import restart.shared.generated.resources.feature_settings_security
import restart.shared.generated.resources.feature_settings_support
import restart.shared.generated.resources.feature_settings_title
import restart.shared.generated.resources.feature_settings_update_password
import restart.shared.generated.resources.feature_theme_config_based_on_time
import restart.shared.generated.resources.feature_theme_config_dark_3
import restart.shared.generated.resources.feature_theme_config_device_1
import restart.shared.generated.resources.feature_theme_config_light_2

@Composable
fun SettingsScreen(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onChangeLanguageClick: () -> Unit,
    onChangeThemeClick: () -> Unit,
    onSupportClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.feature_settings_title))
                },
                actions = {
                    UserInitials(
                        initials = state.initials,
                    )
                },
            )
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            item {
                Spacer(
                    modifier = Modifier.height(8.dp),
                )
            }

            item {
                ProfileCard(
                    userInitials = state.initials,
                    userName = state.username,
                    email = state.email,
                    phone = state.phone,
                    onClick = onProfileClick,
                )
            }

            item {
                SettingsSectionTitle(
                    title =stringResource(Res.string.feature_settings_preferences),
                )
            }

            item {
                SettingsCard {

                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_notifications),
                        subtitle = stringResource(Res.string.feature_settings_notifications_subtitle),
                        onClick = onNotificationsClick,
                    )

                    SettingsDivider()

                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_change_language),
                        subtitle = state.language.languageName,
                        onClick = onChangeLanguageClick,
                    )

                    SettingsDivider()

                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_change_theme),
                        subtitle = themeName(state.theme),
                        onClick = onChangeThemeClick,
                    )
                }
            }

            item {
                SettingsSectionTitle(
                    title = stringResource(Res.string.feature_settings_support),
                )
            }

            item {
                SettingsCard {
                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_get_in_touch),
                        subtitle = stringResource(Res.string.feature_settings_contact_support),
                        onClick = onSupportClick,
                    )
                }
            }

            item {
                SettingsSectionTitle(
                    title = stringResource(Res.string.feature_settings_security),
                )
            }

            item {
                SettingsCard {
                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_change_password),
                        subtitle = stringResource(Res.string.feature_settings_update_password),
                        onClick = onChangePasswordClick,
                    )
                }
            }

            item {
                SettingsSectionTitle(
                    title = stringResource(Res.string.feature_settings_about),
                )
            }

            item {
                SettingsCard {
                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_about_us),
                        subtitle = stringResource(Res.string.feature_settings_learn_more),
                        onClick = onAboutClick,
                    )
                }
            }

            item {
                SettingsSectionTitle(
                    title = stringResource(Res.string.feature_settings_account)
                )
            }

            item {
                SettingsCard {
                    SettingsItem(
                        title = stringResource(Res.string.feature_settings_logout),
                        subtitle = stringResource(Res.string.feature_settings_logout_subtitle),
                        onClick = {
                            viewModel.trySendAction(SettingsAction.LogOutClicked)
                        },
                        titleColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp),
                )
            }
        }
    }

    if (state.dialogState == SettingsState.DialogState.LogoutConfirmation) {
        AlertDialog(
            onDismissRequest = {
                viewModel.trySendAction(SettingsAction.ErrorDismiss)
            },
            title = {
                Text(stringResource(Res.string.feature_settings_logout))
            },
            text = {
                Text(stringResource(Res.string.feature_settings_logout_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.trySendAction(SettingsAction.LogoutConfirmed)
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.feature_settings_logout),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.trySendAction(SettingsAction.ErrorDismiss)
                    }
                ) {
                    Text(stringResource(Res.string.feature_cancel))
                }
            }
        )
    }
}

@Composable
private fun themeName(
    theme: ThemeConfig,
): String {
    return when (theme) {
        ThemeConfig.FOLLOW_SYSTEM -> stringResource(Res.string.feature_theme_config_device_1)
        ThemeConfig.LIGHT -> stringResource(Res.string.feature_theme_config_light_2)
        ThemeConfig.DARK -> stringResource(Res.string.feature_theme_config_dark_3)
        ThemeConfig.BASED_ON_TIME -> stringResource(Res.string.feature_theme_config_based_on_time)
    }
}

@Composable
private fun ProfileCard(
    userInitials: String,
    userName: String,
    email: String,
    phone: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                UserInitials(
                    initials = userInitials,
                )

                Spacer(
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp),
            )

            if (userName.isNotBlank()) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (email.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (phone.isNotBlank()) {
                Spacer(
                    modifier = Modifier.height(2.dp),
                )

                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsDivider() {
    androidx.compose.material3.HorizontalDivider()
}

@Composable
private fun UserInitials(
    initials: String,
) {
    Card(
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp,
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (initials.isBlank()) "?" else initials,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}