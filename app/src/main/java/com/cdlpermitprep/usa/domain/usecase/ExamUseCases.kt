package com.cdlpermitprep.usa.domain.usecase

import com.cdlpermitprep.usa.domain.model.ExamAttempt
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import javax.inject.Inject

class SubmitExamUseCase @Inject constructor(
    private val examRepository: ExamRepository,
) {
    suspend operator fun invoke(
        state: String,
        totalQuestions: Int,
        correctCount: Int,
        durationMs: Long,
        startedAt: Long,
    ): Long {
        val completedAt = System.currentTimeMillis()
        val passRatio = if (totalQuestions == 0) 0.0 else correctCount.toDouble() / totalQuestions
        val attempt = ExamAttempt(
            id = 0,
            state = state,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            durationMs = durationMs,
            passed = passRatio >= 0.8, // CDL general knowledge tests typically require 80%
            startedAt = startedAt,
            completedAt = completedAt,
        )
        return examRepository.saveExamAttempt(attempt)
    }
}
