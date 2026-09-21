package com.victor.restart.feature.theme


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.enums.ThemeConfig
import com.victor.restart.data.TimeBasedTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_cancel
import restart.shared.generated.resources.feature_dark_starts
import restart.shared.generated.resources.feature_light_starts
import restart.shared.generated.resources.feature_save
import restart.shared.generated.resources.feature_theme_config_based_on_time
import restart.shared.generated.resources.feature_theme_config_dark
import restart.shared.generated.resources.feature_theme_config_dark_3
import restart.shared.generated.resources.feature_theme_config_device
import restart.shared.generated.resources.feature_theme_config_device_1
import restart.shared.generated.resources.feature_theme_config_light
import restart.shared.generated.resources.feature_theme_config_light_2
import restart.shared.generated.resources.feature_theme_save
import restart.shared.generated.resources.feature_theme_saving
import restart.shared.generated.resources.feature_theme_schedule
import restart.shared.generated.resources.feature_theme_select_preferred
import restart.shared.generated.resources.feature_theme_title
import restart.shared.generated.resources.feature_theme_toggle_dark_light
import restart.shared.generated.resources.ok

@Composable
fun ChangeThemeScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangeThemeViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                ChangeThemeEvent.NavigateBack -> {
                    navigateBack()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.feature_theme_title)
                    )
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.trySendAction(
                        ChangeThemeAction.SaveTheme
                    )
                },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (state.isSaving) {
                        stringResource(Res.string.feature_theme_saving)
                    } else {
                        stringResource(Res.string.feature_theme_save)
                    }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            item {
                Text(
                    text = stringResource(Res.string.feature_theme_select_preferred),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = 4.dp
                    )
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            items(
                items = ThemeConfig.entries,
                key = { it.name }
            ) { theme ->

                ThemeItem(
                    theme = theme,
                    selected = state.currentTheme == theme,
                    timeBasedTheme = state.timeBasedTheme,
                    onClick = {
                        viewModel.trySendAction(
                            ChangeThemeAction.ThemeSelected(theme)
                        )
                    }
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }

    if (state.showTimeBasedDialog) {
        TimeBasedThemeDialog(
            initialTheme = state.timeBasedTheme,
            onDismiss = {
                viewModel.trySendAction(
                    ChangeThemeAction.HideTimeBasedDialog
                )
            },
            onSave = { theme ->
                viewModel.trySendAction(
                    ChangeThemeAction.UpdateTimeBasedTheme(theme)
                )
            }
        )
    }
}

@Composable
private fun ThemeItem(
    theme: ThemeConfig,
    selected: Boolean,
    timeBasedTheme: TimeBasedTheme,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selected,
                onClick = onClick
            )

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = themeName(theme),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = themeDescription(
                        theme = theme,
                        timeBasedTheme = timeBasedTheme
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
private fun themeDescription(
    theme: ThemeConfig,
    timeBasedTheme: TimeBasedTheme,
): String {
    return when (theme) {

        ThemeConfig.FOLLOW_SYSTEM ->
            stringResource(Res.string.feature_theme_config_device)

        ThemeConfig.LIGHT -> stringResource(Res.string.feature_theme_config_light)

        ThemeConfig.DARK -> stringResource(Res.string.feature_theme_config_dark)

        ThemeConfig.BASED_ON_TIME ->
            "Light ${formatTime(
                timeBasedTheme.hourStart,
                timeBasedTheme.timeStart
            )} - ${formatTime(
                timeBasedTheme.hourEnd,
                timeBasedTheme.timeEnd
            )}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeBasedThemeDialog(
    initialTheme: TimeBasedTheme,
    onDismiss: () -> Unit,
    onSave: (TimeBasedTheme) -> Unit,
) {
    var lightStartHour by remember(
        initialTheme.hourStart,
        initialTheme.timeStart
    ) {
        mutableStateOf(initialTheme.hourStart)
    }

    var lightStartMinute by remember(
        initialTheme.hourStart,
        initialTheme.timeStart
    ) {
        mutableStateOf(initialTheme.timeStart)
    }

    var darkStartHour by remember(
        initialTheme.hourEnd,
        initialTheme.timeEnd
    ) {
        mutableStateOf(initialTheme.hourEnd)
    }

    var darkStartMinute by remember(
        initialTheme.hourEnd,
        initialTheme.timeEnd
    ) {
        mutableStateOf(initialTheme.timeEnd)
    }

    var showLightTimePicker by remember {
        mutableStateOf(false)
    }

    var showDarkTimePicker by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.feature_theme_schedule)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = stringResource(Res.string.feature_theme_toggle_dark_light),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                TimeRow(
                    title = stringResource(Res.string.feature_light_starts),
                    time = formatTime(
                        lightStartHour,
                        lightStartMinute
                    ),
                    onClick = {
                        showLightTimePicker = true
                    }
                )

                TimeRow(
                    title = stringResource(Res.string.feature_dark_starts),
                    time = formatTime(
                        darkStartHour,
                        darkStartMinute
                    ),
                    onClick = {
                        showDarkTimePicker = true
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        TimeBasedTheme(
                            hourStart = lightStartHour,
                            hourEnd = darkStartHour,
                            timeStart = lightStartMinute,
                            timeEnd = darkStartMinute,
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.feature_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.feature_cancel))
            }
        }
    )

    if (showLightTimePicker) {

        val timePickerState = rememberTimePickerState(
            initialHour = lightStartHour,
            initialMinute = lightStartMinute,
            is24Hour = false
        )

        TimePickerDialog(
            title = "Light theme starts",
            timePickerState = timePickerState,
            onDismiss = {
                showLightTimePicker = false
            },
            onConfirm = {
                lightStartHour = timePickerState.hour
                lightStartMinute = timePickerState.minute
                showLightTimePicker = false
            }
        )
    }

    if (showDarkTimePicker) {

        val timePickerState = rememberTimePickerState(
            initialHour = darkStartHour,
            initialMinute = darkStartMinute,
            is24Hour = false
        )

        TimePickerDialog(
            title = stringResource(Res.string.feature_dark_starts),
            timePickerState = timePickerState,
            onDismiss = {
                showDarkTimePicker = false
            },
            onConfirm = {
                darkStartHour = timePickerState.hour
                darkStartMinute = timePickerState.minute
                showDarkTimePicker = false
            }
        )
    }
}

@Composable
private fun TimeRow(
    title: String,
    time: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    title: String,
    timePickerState: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title
            )
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.feature_cancel))
            }
        }
    )
}

private fun formatTime(
    hour: Int,
    minute: Int,
): String {
    val period = if (hour >= 12) "PM" else "AM"

    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "$displayHour:${minute.toString().padStart(2, '0')} $period"
}