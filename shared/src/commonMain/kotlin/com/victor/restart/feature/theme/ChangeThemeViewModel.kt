package com.victor.restart.feature.theme

import androidx.lifecycle.viewModelScope
import com.victor.restart.core.enums.ThemeConfig
import com.victor.restart.core.repository.userdata.UserPreferencesRepository
import com.victor.restart.core.utils.BaseViewModel
import com.victor.restart.data.TimeBasedTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeThemeViewModel(
    private val repository: UserPreferencesRepository,
) : BaseViewModel<ChangeThemeState, ChangeThemeEvent, ChangeThemeAction>(
    initialState = ChangeThemeState(
        currentTheme = ThemeConfig.FOLLOW_SYSTEM,
        showTimeBasedDialog = false,
        timeBasedTheme = TimeBasedTheme(
            hourStart = 6,
            hourEnd = 18,
            timeStart = 0,
            timeEnd = 0,
        ),
    ),
) {

    init {
        observeTheme()
        observeTimeBasedTheme()
    }


    private fun observeTheme() {
        repository.observeDarkThemeConfig
            .map { theme ->
                ChangeThemeAction.Internal.LoadTheme(theme)
            }
            .onEach(::trySendAction)
            .launchIn(viewModelScope)
    }

    private fun observeTimeBasedTheme() {
        repository.observeTimeBasedThemeConfig
            .map { theme ->
                ChangeThemeAction.Internal.LoadTimeBasedTheme(theme)
            }
            .onEach(::trySendAction)
            .launchIn(viewModelScope)
    }

    private fun updateState(block: (ChangeThemeState) -> ChangeThemeState, ) {
        mutableStateFlow.update(block)
    }


    override fun handleAction(action: ChangeThemeAction) {
        when (action) {

            is ChangeThemeAction.ThemeSelected -> {
                when (action.theme) {

                    ThemeConfig.BASED_ON_TIME -> {
                        updateState {
                            it.copy(
                                currentTheme = ThemeConfig.BASED_ON_TIME,
                                showTimeBasedDialog = true,
                            )
                        }
                    }

                    else -> {
                        updateState {
                            it.copy(
                                currentTheme = action.theme,
                                showTimeBasedDialog = false,
                            )
                        }
                    }
                }
            }

            ChangeThemeAction.SaveTheme -> {
                saveTheme()
            }

            ChangeThemeAction.NavigateBack -> {
                sendEvent(ChangeThemeEvent.NavigateBack)
            }

            ChangeThemeAction.HideTimeBasedDialog -> {
                updateState {
                    it.copy(
                        showTimeBasedDialog = false,
                    )
                }
            }

            is ChangeThemeAction.UpdateTimeBasedTheme -> {
                updateTimeBasedTheme(action.theme)
            }

            is ChangeThemeAction.Internal.LoadTheme -> {
                updateState {
                    it.copy(
                        currentTheme = action.theme,
                    )
                }
            }

            is ChangeThemeAction.Internal.LoadTimeBasedTheme -> {
                updateState {
                    it.copy(
                        timeBasedTheme = action.theme,
                    )
                }
            }
        }
    }

    private fun updateTimeBasedTheme(theme: TimeBasedTheme, ) {
        viewModelScope.launch {

            updateState {
                it.copy(
                    isSaving = true,
                )
            }

            repository.updateTimeBasedTheme(theme)

            repository.updateTheme(
                ThemeConfig.BASED_ON_TIME
            )

            updateState {
                it.copy(
                    currentTheme = ThemeConfig.BASED_ON_TIME,
                    timeBasedTheme = theme,
                    showTimeBasedDialog = false,
                    isSaving = false,
                )
            }
        }
    }

    private fun saveTheme() {
        viewModelScope.launch {

            updateState {
                it.copy(
                    isSaving = true,
                )
            }

            repository.updateTheme(state.currentTheme)

            updateState {
                it.copy(
                    isSaving = false,
                )
            }

            sendEvent(
                ChangeThemeEvent.NavigateBack
            )
        }
    }
}

data class ChangeThemeState(
    val currentTheme: ThemeConfig,
    val showTimeBasedDialog: Boolean = false,
    val timeBasedTheme: TimeBasedTheme = TimeBasedTheme(
        hourStart = 6,
        hourEnd = 18,
        timeStart = 0,
        timeEnd = 0,
    ),
    val isSaving: Boolean = false,
)

sealed interface ChangeThemeEvent {
    data object NavigateBack : ChangeThemeEvent
}

sealed interface ChangeThemeAction {

    data class ThemeSelected(val theme: ThemeConfig, ) : ChangeThemeAction

    data object SaveTheme : ChangeThemeAction

    data object NavigateBack : ChangeThemeAction

    data object HideTimeBasedDialog : ChangeThemeAction

    data class UpdateTimeBasedTheme(val theme: TimeBasedTheme, ) : ChangeThemeAction

    sealed interface Internal : ChangeThemeAction {

        data class LoadTheme(val theme: ThemeConfig, ) : Internal

        data class LoadTimeBasedTheme(val theme: TimeBasedTheme, ) : Internal
    }
}