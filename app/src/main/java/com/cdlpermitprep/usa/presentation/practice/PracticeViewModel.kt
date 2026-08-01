package com.cdlpermitprep.usa.presentation.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.ai.AnswerExplanationProvider
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.PracticeMode
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.BookmarkRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import com.cdlpermitprep.usa.domain.repository.UserRepository
import com.cdlpermitprep.usa.domain.usecase.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeUiState(
    val loading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: AnswerOption? = null,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val isBookmarked: Boolean = false,
    val liveExplanation: String? = null,
    val finished: Boolean = false,
    val secondsRemaining: Int? = null, // non-null when timed mode
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val progress: Float get() = if (questions.isEmpty()) 0f else (currentIndex + 1f) / questions.size
}

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val userRepository: UserRepository,
    private val answerExplanationProvider: AnswerExplanationProvider,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val mode = PracticeMode.valueOf(savedStateHandle.get<String>("mode") ?: PracticeMode.RANDOM.name)
    private val categoryName = savedStateHandle.get<String>("categoryName")?.takeIf { it != "all" }

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var questionStartMillis = 0L

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val count = if (mode == PracticeMode.DAILY_CHALLENGE) 10 else 20
            val questions = questionRepository.randomQuestions(state = null, category = categoryName, limit = count)
            _uiState.value = _uiState.value.copy(
                loading = false,
                questions = questions,
                secondsRemaining = if (mode == PracticeMode.TIMED) 45 else null,
            )
            questionStartMillis = System.currentTimeMillis()
        }
    }

    fun selectAnswer(answer: AnswerOption) {
        val current = _uiState.value
        if (current.isAnswerRevealed) return
        val question = current.currentQuestion ?: return
        val isCorrect = answer == question.correctAnswer
        val timeSpent = System.currentTimeMillis() - questionStartMillis

        viewModelScope.launch {
            submitAnswerUseCase(question, answer, timeSpent, mode = mode.name)
            userRepository.awardXp(if (isCorrect) 10 else 2)
            userRepository.recordStudySession()
            val explanation = answerExplanationProvider.explain(question, answer.name)
            _uiState.value = _uiState.value.copy(
                selectedAnswer = answer,
                isAnswerRevealed = true,
                correctCount = current.correctCount + if (isCorrect) 1 else 0,
                liveExplanation = explanation,
            )
        }
    }

    /** Called when the timed-practice countdown hits zero without a selection; counts as wrong. */
    private fun timeOut() {
        val current = _uiState.value
        if (current.isAnswerRevealed) return
        val question = current.currentQuestion ?: return
        val wrongOption = AnswerOption.values().first { it != question.correctAnswer }
        selectAnswer(wrongOption)
    }

    fun toggleBookmark() {
        val question = _uiState.value.currentQuestion ?: return
        val newValue = !_uiState.value.isBookmarked
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(question.id, newValue)
            _uiState.value = _uiState.value.copy(isBookmarked = newValue)
        }
    }

    fun nextQuestion() {
        val current = _uiState.value
        if (current.currentIndex + 1 >= current.questions.size) {
            _uiState.value = current.copy(finished = true)
            return
        }
        _uiState.value = current.copy(
            currentIndex = current.currentIndex + 1,
            selectedAnswer = null,
            isAnswerRevealed = false,
            isBookmarked = false,
            liveExplanation = null,
            secondsRemaining = if (mode == PracticeMode.TIMED) 45 else null,
        )
        questionStartMillis = System.currentTimeMillis()
    }

    fun onTimerTick() {
        val seconds = _uiState.value.secondsRemaining ?: return
        if (seconds <= 0) {
            timeOut()
            return
        }
        _uiState.value = _uiState.value.copy(secondsRemaining = seconds - 1)
    }
}
