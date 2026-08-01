package com.cdlpermitprep.usa.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.model.AnalyticsSummary
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    examRepository: ExamRepository,
) : ViewModel() {
    val summary: StateFlow<AnalyticsSummary?> = examRepository.analyticsSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
