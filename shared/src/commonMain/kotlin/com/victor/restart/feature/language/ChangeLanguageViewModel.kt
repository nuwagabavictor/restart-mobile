package com.victor.restart.feature.language


import androidx.lifecycle.viewModelScope
import com.victor.restart.core.enums.LanguageConfig
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.core.utils.ScreenUiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeLanguageViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<ChangeLanguageState, ChangeLanguageEvent, ChangeLanguageAction>(
    initialState = ChangeLanguageState(
        currentLanguage = LanguageConfig.DEFAULT,
        selectedLanguage = LanguageConfig.DEFAULT,
        uiState = ScreenUiState.Success,
    ),
) {

    init {
        userPreferencesRepository.observeLanguage
            .map { language ->
                ChangeLanguageAction.Internal.LoadLanguage(language)
            }
            .onEach(::trySendAction)
            .launchIn(viewModelScope)
    }

    private fun updateState(block: (ChangeLanguageState) -> ChangeLanguageState) {
        mutableStateFlow.update(block)
    }

    override fun handleAction(action: ChangeLanguageAction) {

        when (action) {

            ChangeLanguageAction.NavigateBack -> {
                sendEvent(ChangeLanguageEvent.NavigateBack)
            }

            is ChangeLanguageAction.LanguageSelected -> {
                updateState {
                    it.copy(
                        selectedLanguage = action.language,
                    )
                }
            }

            is ChangeLanguageAction.SaveLanguage -> {
                saveLanguage(action.language)
            }

            is ChangeLanguageAction.Internal.LoadLanguage -> {
                updateState {
                    it.copy(
                        currentLanguage = action.language,
                        selectedLanguage = action.language,
                    )
                }
            }
        }
    }

    private fun saveLanguage(language: LanguageConfig) {

        viewModelScope.launch {

            updateState {
                it.copy(isSaving = true)
            }

            userPreferencesRepository.setLanguage(language)

            updateState {
                it.copy(
                    currentLanguage = language,
                    selectedLanguage = language,
                    isSaving = false,
                )
            }

            sendEvent(ChangeLanguageEvent.NavigateBack)
        }
    }
}


data class ChangeLanguageState(

    val currentLanguage: LanguageConfig,

    val selectedLanguage: LanguageConfig,

    val isSaving: Boolean = false,

    val uiState: ScreenUiState,
)


sealed interface ChangeLanguageEvent {

    data object NavigateBack : ChangeLanguageEvent
}


sealed interface ChangeLanguageAction {

    data object NavigateBack : ChangeLanguageAction

    data class LanguageSelected(val language: LanguageConfig, ) : ChangeLanguageAction

    data class SaveLanguage(val language: LanguageConfig, ) : ChangeLanguageAction

    sealed interface Internal : ChangeLanguageAction {

        data class LoadLanguage(val language: LanguageConfig, ) : Internal
    }
}