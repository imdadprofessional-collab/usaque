package com.cdlpermitprep.usa.domain.model

data class ExamAttempt(
    val id: Long,
    val state: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val durationMs: Long,
    val passed: Boolean,
    val startedAt: Long,
    val completedAt: Long,
) {
    val scorePercent: Int get() = if (totalQuestions == 0) 0 else (correctCount * 100) / totalQuestions
}

data class ExamReviewItem(
    val question: Question,
    val selectedAnswer: AnswerOption?,
    val isCorrect: Boolean,
)

data class AnalyticsSummary(
    val totalAnswered: Int,
    val totalCorrect: Int,
    val accuracyPercent: Int,
    val averageTimeSeconds: Int,
    val accuracyByCategory: List<CategoryAccuracyStat>,
    val readinessScore: Int,
    val passPrediction: PassPrediction,
)

data class CategoryAccuracyStat(
    val category: String,
    val correct: Int,
    val total: Int,
) {
    val accuracyPercent: Int get() = if (total == 0) 0 else (correct * 100) / total
}

enum class PassPrediction { LIKELY_PASS, NEEDS_MORE_PRACTICE, AT_RISK, NOT_ENOUGH_DATA }
