package com.cdlpermitprep.usa.presentation.mockexam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import com.cdlpermitprep.usa.domain.usecase.SubmitAnswerUseCase
import com.cdlpermitprep.usa.domain.usecase.SubmitExamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EXAM_QUESTION_COUNT = 25
private const val EXAM_DURATION_SECONDS = 30 * 60 // 30 minutes, matches typical CDL general knowledge test pacing

data class MockExamUiState(
    val loading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Long, AnswerOption> = emptyMap(),
    val secondsRemaining: Int = EXAM_DURATION_SECONDS,
    val isPaused: Boolean = false,
    val submittedAttemptId: Long? = null,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val progress: Float get() = if (questions.isEmpty()) 0f else (currentIndex + 1f) / questions.size
}

@HiltViewModel
class MockExamViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val submitExamUseCase: SubmitExamUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MockExamUiState())
    val uiState: StateFlow<MockExamUiState> = _uiState.asStateFlow()

    private var startedAt = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            val state = userPreferences.selectedState.first()
            val questions = questionRepository.randomQuestions(state = state, category = null, limit = EXAM_QUESTION_COUNT)
            _uiState.value = _uiState.value.copy(loading = false, questions = questions)
            startedAt = System.currentTimeMillis()
        }
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    fun onTimerTick() {
        val current = _uiState.value
        if (current.isPaused || current.submittedAttemptId != null) return
        if (current.secondsRemaining <= 0) {
            submitExam()
            return
        }
        _uiState.value = current.copy(secondsRemaining = current.secondsRemaining - 1)
    }

    fun selectAnswer(answer: AnswerOption) {
        val question = _uiState.value.currentQuestion ?: return
        _uiState.value = _uiState.value.copy(answers = _uiState.value.answers + (question.id to answer))
    }

    fun goToQuestion(index: Int) {
        if (index in _uiState.value.questions.indices) {
            _uiState.value = _uiState.value.copy(currentIndex = index)
        }
    }

    fun nextQuestion() = goToQuestion(_uiState.value.currentIndex + 1)
    fun previousQuestion() = goToQuestion(_uiState.value.currentIndex - 1)

    fun submitExam() {
        val current = _uiState.value
        if (current.submittedAttemptId != null) return
        viewModelScope.launch {
            var correctCount = 0
            current.questions.forEach { question ->
                val selected = current.answers[question.id]
                val isCorrect = selected != null && selected == question.correctAnswer
                if (isCorrect) correctCount++
                if (selected != null) {
                    submitAnswerUseCase(question, selected, timeSpentMs = 0, mode = "MOCK_EXAM")
                }
            }
            val state = userPreferences.selectedState.first()
            val attemptId = submitExamUseCase(
                state = state,
                totalQuestions = current.questions.size,
                correctCount = correctCount,
                durationMs = System.currentTimeMillis() - startedAt,
                startedAt = startedAt,
            )
            _uiState.value = current.copy(submittedAttemptId = attemptId)
        }
    }
}
