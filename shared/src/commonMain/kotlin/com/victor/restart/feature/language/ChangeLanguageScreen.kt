package com.victor.restart.feature.language


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.victor.restart.core.enums.LanguageConfig
import org.koin.compose.viewmodel.koinViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChangeLanguageScreen(
    onBackClick: () -> Unit,
    viewModel: ChangeLanguageViewModel = koinViewModel(),
) {
    val state = viewModel.stateFlow.collectAsStateWithLifecycle().value

    ChangeLanguageContent(
        state = state,
        onBackClick = onBackClick,
        onLanguageSelected = {
            viewModel.trySendAction(
                ChangeLanguageAction.LanguageSelected(it)
            )
        },
        onSaveClick = {
            viewModel.trySendAction(
                ChangeLanguageAction.SaveClicked
            )
        },
    )
}

@Composable
private fun ChangeLanguageContent(
    state: ChangeLanguageState,
    onBackClick: () -> Unit,
    onLanguageSelected: (com.victor.restart.core.enums.LanguageConfig) -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Change Language")
                },
                navigationIcon = {
                    Text(
                        text = "‹",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable(onClick = onBackClick),
                    )
                },
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {

            Text(
                text = "Select your preferred language",
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 12.dp,
                ),
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(LanguageConfig.entries) { language ->

                    LanguageItem(
                        language = language,
                        selected = language == state.selectedLanguage,
                        onClick = {
                            onLanguageSelected(language)
                        },
                    )

                    HorizontalDivider()
                }
            }

            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: LanguageConfig,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = language.languageName,
            )

            language.locale?.let {
                Text(
                    text = it,
                )
            }
        }

        RadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}