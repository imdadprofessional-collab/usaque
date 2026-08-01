package com.cdlpermitprep.usa.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.model.ExamAttempt
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val attemptId: Long = savedStateHandle.get<Long>("attemptId") ?: 0L

    private val _attempt = MutableStateFlow<ExamAttempt?>(null)
    val attempt: StateFlow<ExamAttempt?> = _attempt.asStateFlow()

    init {
        viewModelScope.launch {
            _attempt.value = examRepository.examHistory().first().firstOrNull { it.id == attemptId }
        }
    }
}
