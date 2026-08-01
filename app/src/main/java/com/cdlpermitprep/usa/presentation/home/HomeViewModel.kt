package com.cdlpermitprep.usa.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.domain.model.AnalyticsSummary
import com.cdlpermitprep.usa.domain.model.Category
import com.cdlpermitprep.usa.domain.model.GamificationSnapshot
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import com.cdlpermitprep.usa.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

private val PREMIUM_CATEGORIES = setOf("Hazardous Materials", "Tank Vehicles", "Doubles/Triples", "Passenger Vehicles")

private data class HomePrefs(val name: String, val state: String, val goal: Int, val premium: Boolean)

data class HomeUiState(
    val userName: String = "Driver",
    val selectedState: String = "General",
    val dailyGoal: Int = 20,
    val categories: List<Category> = emptyList(),
    val gamification: GamificationSnapshot = GamificationSnapshot(0, 1, 0, 500),
    val analytics: AnalyticsSummary? = null,
    val isPremium: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val categories = questionRepository.getCategories().map { name ->
                Category(name = name, questionCount = 0, isPremium = name in PREMIUM_CATEGORIES)
            }
            _uiState.value = _uiState.value.copy(categories = categories)
        }
        viewModelScope.launch {
            val prefsCombined = combine(
                userPreferences.userName,
                userPreferences.selectedState,
                userPreferences.dailyGoal,
                userPreferences.isPremium,
            ) { name, state, goal, premium -> HomePrefs(name, state, goal, premium) }

            combine(
                prefsCombined,
                userRepository.gamification(),
                examRepository.analyticsSummary(),
            ) { prefs, gamification, analytics ->
                _uiState.value = _uiState.value.copy(
                    userName = prefs.name,
                    selectedState = prefs.state,
                    dailyGoal = prefs.goal,
                    isPremium = prefs.premium,
                    gamification = gamification,
                    analytics = analytics,
                )
            }.collect {}
        }
    }
}
