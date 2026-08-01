package com.cdlpermitprep.usa.domain.usecase

import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.model.isAccessibleWith
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Fetches random practice/exam questions, filtered down to what the user is actually entitled
 * to see: free content always, premium content only with a full subscription/lifetime purchase
 * or ownership of that question's specific pack. Over-fetches candidates from the repository
 * (cheap at this bank's current size) so filtering still returns close to [limit] results; at
 * true 100k+ scale this filter should move into the SQL query alongside an entitlement column.
 */
class GetRandomQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val billingRepository: BillingRepository,
) {
    suspend operator fun invoke(state: String?, category: String?, limit: Int): List<Question> {
        val isPremium = billingRepository.premiumStatus().first()
        val ownedPacks = billingRepository.ownedPackIds().first()
        val candidates = questionRepository.randomQuestions(state, category, limit * OVER_FETCH_FACTOR)
        return candidates
            .filter { it.isAccessibleWith(isPremium, ownedPacks) }
            .take(limit)
    }

    private companion object {
        const val OVER_FETCH_FACTOR = 4
    }
}

class SubmitAnswerUseCase @Inject constructor(
    private val examRepository: ExamRepository,
) {
    suspend operator fun invoke(
        question: Question,
        selected: AnswerOption,
        timeSpentMs: Long,
        mode: String,
    ) {
        examRepository.recordAnswer(
            questionId = question.id,
            category = question.category,
            selected = selected,
            isCorrect = selected == question.correctAnswer,
            timeSpentMs = timeSpentMs,
            mode = mode,
        )
    }
}
