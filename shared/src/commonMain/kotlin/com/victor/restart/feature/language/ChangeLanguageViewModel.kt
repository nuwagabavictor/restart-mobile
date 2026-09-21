package com.victor.restart.feature.language

import com.victor.restart.core.enums.LanguageConfig
import androidx.lifecycle.viewModelScope
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.DataState
import com.victor.restart.core.utils.ScreenUiState
import com.victor.restart.feature.settings.SettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeLanguageViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<ChangeLanguageState, ChangeLanguageEvent, ChangeLanguageAction>(
    initialState = ChangeLanguageState(uiState = ScreenUiState.Success),
) {

    private var languageJob: Job? = null
    private var saveJob: Job? = null

    init {
        trySendAction(ChangeLanguageAction.Load)
    }

    private fun updateState(block: (ChangeLanguageState) -> ChangeLanguageState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: ChangeLanguageAction) {
        when (action) {

            ChangeLanguageAction.Load -> {
                loadLanguage()
            }

            is ChangeLanguageAction.LanguageSelected -> {
                updateState {
                    it.copy(
                        selectedLanguage = action.language,
                        isError = false,
                        errorMessage = null,
                    )
                }
            }

            ChangeLanguageAction.SaveClicked -> {
                saveLanguage()
            }

            ChangeLanguageAction.ErrorDismiss -> {
                updateState {
                    it.copy(
                        isError = false,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    private fun loadLanguage() {
        languageJob?.cancel()

        languageJob = viewModelScope.launch {
            userPreferencesRepository.observeLanguage
                .collectLatest { language ->
                    updateState {
                        it.copy(
                            selectedLanguage = language,
                            isLoading = false,
                            isError = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private fun saveLanguage() {
        saveJob?.cancel()

        val language = state.selectedLanguage

        saveJob = viewModelScope.launch {
            updateState {
                it.copy(
                    isSaving = true,
                    isError = false,
                    errorMessage = null,
                )
            }

            try {
                userPreferencesRepository.setLanguage(language)

                updateState {
                    it.copy(
                        isSaving = false,
                        isError = false,
                        errorMessage = null,
                    )
                }

                sendEvent(ChangeLanguageEvent.NavigateBack)

            } catch (exception: Exception) {
                val message = exception.message ?: "Failed to change language"

                updateState {
                    it.copy(
                        isSaving = false,
                        isError = true,
                        errorMessage = message,
                    )
                }

                sendEvent(
                    ChangeLanguageEvent.ShowError(message)
                )
            }
        }
    }
}

data class ChangeLanguageState(
    val selectedLanguage: LanguageConfig = LanguageConfig.DEFAULT,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val uiState: ScreenUiState
)

sealed interface ChangeLanguageAction {

    data object Load : ChangeLanguageAction

    data class LanguageSelected(val language: LanguageConfig) : ChangeLanguageAction

    data object SaveClicked : ChangeLanguageAction

    data object ErrorDismiss : ChangeLanguageAction
}

sealed interface ChangeLanguageEvent {

    data object NavigateBack : ChangeLanguageEvent

    data class ShowError(val message: String) : ChangeLanguageEvent
}