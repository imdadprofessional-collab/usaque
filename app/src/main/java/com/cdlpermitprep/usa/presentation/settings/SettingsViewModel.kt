package com.cdlpermitprep.usa.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: String = "system",
    val dailyGoal: Int = 20,
    val notificationsEnabled: Boolean = true,
    val selectedState: String = "General",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val uiState = combine(
        userPreferences.darkMode,
        userPreferences.dailyGoal,
        userPreferences.notificationsEnabled,
        userPreferences.selectedState,
    ) { darkMode, goal, notifications, state ->
        SettingsUiState(darkMode, goal, notifications, state)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDarkMode(mode: String) = viewModelScope.launch { userPreferences.setDarkMode(mode) }
    fun setDailyGoal(goal: Int) = viewModelScope.launch { userPreferences.setDailyGoal(goal) }
    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch { userPreferences.setNotificationsEnabled(enabled) }
    fun setSelectedState(state: String) = viewModelScope.launch { userPreferences.setSelectedState(state) }
}
