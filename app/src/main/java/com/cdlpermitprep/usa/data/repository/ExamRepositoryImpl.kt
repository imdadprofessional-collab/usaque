package com.cdlpermitprep.usa.data.repository

import com.cdlpermitprep.usa.data.local.dao.AnswerRecordDao
import com.cdlpermitprep.usa.data.local.dao.ExamAttemptDao
import com.cdlpermitprep.usa.data.local.entity.AnswerRecordEntity
import com.cdlpermitprep.usa.domain.model.AnalyticsSummary
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.CategoryAccuracyStat
import com.cdlpermitprep.usa.domain.model.ExamAttempt
import com.cdlpermitprep.usa.domain.model.PassPrediction
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepositoryImpl @Inject constructor(
    private val answerRecordDao: AnswerRecordDao,
    private val examAttemptDao: ExamAttemptDao,
) : ExamRepository {

    override suspend fun recordAnswer(
        questionId: Long,
        category: String,
        selected: AnswerOption,
        isCorrect: Boolean,
        timeSpentMs: Long,
        mode: String,
    ) {
        answerRecordDao.insert(
            AnswerRecordEntity(
                questionId = questionId,
                category = category,
                selectedAnswer = selected.name,
                isCorrect = isCorrect,
                timeSpentMs = timeSpentMs,
                mode = mode,
            ),
        )
    }

    override suspend fun saveExamAttempt(attempt: ExamAttempt): Long = examAttemptDao.insert(attempt.toEntity())

    override fun examHistory(): Flow<List<ExamAttempt>> =
        examAttemptDao.history().map { list -> list.map { it.toDomain() } }

    override fun analyticsSummary(): Flow<AnalyticsSummary> = combine(
        answerRecordDao.totalAnsweredFlow(),
        answerRecordDao.totalCorrectFlow(),
        answerRecordDao.accuracyByCategory(),
        answerRecordDao.averageTimeMsFlow(),
        examAttemptDao.passRateFlow(),
    ) { values ->
        val totalAnswered = values[0] as Int
        val totalCorrect = values[1] as Int
        @Suppress("UNCHECKED_CAST")
        val byCategory = values[2] as List<com.cdlpermitprep.usa.data.local.dao.CategoryAccuracy>
        val avgTimeMs = values[3] as Double?
        val passRate = values[4] as Double?

        val accuracyPercent = if (totalAnswered == 0) 0 else (totalCorrect * 100) / totalAnswered
        val categoryStats = byCategory.map { CategoryAccuracyStat(it.category, it.correct, it.total) }
        val readiness = computeReadinessScore(accuracyPercent, totalAnswered, passRate)

        AnalyticsSummary(
            totalAnswered = totalAnswered,
            totalCorrect = totalCorrect,
            accuracyPercent = accuracyPercent,
            averageTimeSeconds = ((avgTimeMs ?: 0.0) / 1000).toInt(),
            accuracyByCategory = categoryStats,
            readinessScore = readiness,
            passPrediction = predictOutcome(totalAnswered, readiness),
        )
    }

    private fun computeReadinessScore(accuracyPercent: Int, totalAnswered: Int, passRate: Double?): Int {
        if (totalAnswered == 0) return 0
        val volumeFactor = (totalAnswered.coerceAtMost(200) / 200.0) * 20 // up to 20 pts for practice volume
        val accuracyFactor = accuracyPercent * 0.6 // up to 60 pts
        val examFactor = (passRate ?: 0.0) * 0.2 // up to 20 pts
        return (volumeFactor + accuracyFactor + examFactor).toInt().coerceIn(0, 100)
    }

    private fun predictOutcome(totalAnswered: Int, readiness: Int): PassPrediction = when {
        totalAnswered < 20 -> PassPrediction.NOT_ENOUGH_DATA
        readiness >= 80 -> PassPrediction.LIKELY_PASS
        readiness >= 60 -> PassPrediction.NEEDS_MORE_PRACTICE
        else -> PassPrediction.AT_RISK
    }
}
