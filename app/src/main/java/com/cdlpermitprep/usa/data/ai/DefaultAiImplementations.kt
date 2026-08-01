package com.cdlpermitprep.usa.data.ai

import com.cdlpermitprep.usa.domain.ai.AnswerExplanationProvider
import com.cdlpermitprep.usa.domain.ai.PersonalTutor
import com.cdlpermitprep.usa.domain.ai.QuestionRecommendationEngine
import com.cdlpermitprep.usa.domain.ai.StudyPlanDay
import com.cdlpermitprep.usa.domain.ai.StudyPlanner
import com.cdlpermitprep.usa.domain.ai.TutorMessage
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Rule-based defaults so the app is fully functional offline today. Each of these
 * can be replaced by an LLM-backed implementation later purely via Hilt binding changes.
 */
class LocalAnswerExplanationProvider @Inject constructor() : AnswerExplanationProvider {
    override suspend fun explain(question: Question, selectedAnswer: String): String =
        if (selectedAnswer == question.correctAnswer.name) {
            "Correct! ${question.explanation}"
        } else {
            "Not quite. ${question.explanation} Review the '${question.subCategory}' topic for more like this."
        }
}

class LocalStudyPlanner @Inject constructor() : StudyPlanner {
    override suspend fun generatePlan(examDateEpochMillis: Long?, weakCategories: List<String>): List<StudyPlanDay> {
        val daysUntilExam = examDateEpochMillis?.let {
            ((it - System.currentTimeMillis()) / TimeUnit.DAYS.toMillis(1)).toInt().coerceAtLeast(1)
        } ?: 14
        val focus = weakCategories.ifEmpty { listOf("General Knowledge") }
        return (1..daysUntilExam.coerceAtMost(14)).map { day ->
            StudyPlanDay(
                dayLabel = "Day $day",
                focusCategories = listOf(focus[(day - 1) % focus.size]),
                targetQuestions = 20,
            )
        }
    }
}

class LocalPersonalTutor @Inject constructor() : PersonalTutor {
    override suspend fun ask(conversation: List<TutorMessage>, question: String): String =
        "That's a great question about \"$question\". Full AI tutoring isn't wired up yet — " +
            "check the explanation on each question or the Analytics tab for now."
}

class LocalQuestionRecommendationEngine @Inject constructor(
    private val questionRepository: QuestionRepository,
) : QuestionRecommendationEngine {
    override suspend fun recommendNext(weakCategories: List<String>, limit: Int): List<Question> {
        val category = weakCategories.firstOrNull()
        return questionRepository.randomQuestions(state = null, category = category, limit = limit)
    }
}
