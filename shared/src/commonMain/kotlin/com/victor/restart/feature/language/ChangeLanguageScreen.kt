package com.victor.restart.feature.language


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.enums.LanguageConfig
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import restart.shared.generated.resources.Res
import restart.shared.generated.resources.feature_language_save
import restart.shared.generated.resources.feature_language_select_description
import restart.shared.generated.resources.feature_settings_change_language

@Composable
fun ChangeLanguageScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangeLanguageViewModel = koinViewModel(),
) {

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                ChangeLanguageEvent.NavigateBack -> navigateBack()
            }
        }
    }

    ChangeLanguageContent(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun ChangeLanguageContent(
    state: ChangeLanguageState,
    modifier: Modifier = Modifier,
    onAction: (ChangeLanguageAction) -> Unit,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.feature_settings_change_language)) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            onAction(ChangeLanguageAction.NavigateBack)
                        },
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        },
        bottomBar = {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                enabled = !state.isSaving,
                onClick = {
                    onAction(
                        ChangeLanguageAction.SaveLanguage(
                            state.selectedLanguage,
                        ),
                    )
                },
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(Res.string.feature_language_save))
                }
            }
        },
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            item {
                Text(stringResource(Res.string.feature_language_select_description))
            }

            items(LanguageConfig.entries) { language ->

                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    headlineContent = {
                        Text(language.languageName)
                    },
                    supportingContent = {
                        language.locale?.let {
                            Text(it)
                        }
                    },
                    leadingContent = {
                        RadioButton(
                            selected = language == state.selectedLanguage,
                            onClick = {
                                onAction(
                                    ChangeLanguageAction.LanguageSelected(
                                        language,
                                    ),
                                )
                            },
                        )
                    },
                )
            }
        }
    }
}