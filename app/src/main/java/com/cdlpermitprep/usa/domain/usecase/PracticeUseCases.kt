package com.cdlpermitprep.usa.domain.usecase

import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import javax.inject.Inject

class GetRandomQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(state: String?, category: String?, limit: Int): List<Question> =
        questionRepository.randomQuestions(state, category, limit)
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
