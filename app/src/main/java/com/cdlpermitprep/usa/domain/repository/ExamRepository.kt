package com.cdlpermitprep.usa.domain.repository

import com.cdlpermitprep.usa.domain.model.AnalyticsSummary
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.ExamAttempt
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    suspend fun recordAnswer(
        questionId: Long,
        category: String,
        selected: AnswerOption,
        isCorrect: Boolean,
        timeSpentMs: Long,
        mode: String,
    )

    suspend fun saveExamAttempt(attempt: ExamAttempt): Long
    fun examHistory(): Flow<List<ExamAttempt>>
    fun analyticsSummary(): Flow<AnalyticsSummary>
}
