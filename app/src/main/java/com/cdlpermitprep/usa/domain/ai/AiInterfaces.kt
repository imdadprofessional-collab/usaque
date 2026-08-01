package com.cdlpermitprep.usa.domain.ai

import com.cdlpermitprep.usa.domain.model.Question

/**
 * Forward-looking seams for AI features. Nothing in `presentation` should depend on a
 * concrete implementation of these — only on the interface, injected via Hilt. Today's
 * default bindings can be simple rule-based/local implementations; a future LLM-backed
 * implementation can swap in without touching ViewModels or Composables.
 */

/** Generates a richer, personalized explanation for why an answer is right/wrong. */
interface AnswerExplanationProvider {
    suspend fun explain(question: Question, selectedAnswer: String): String
}

data class StudyPlanDay(val dayLabel: String, val focusCategories: List<String>, val targetQuestions: Int)

/** Builds a day-by-day plan from now until the user's exam date. */
interface StudyPlanner {
    suspend fun generatePlan(examDateEpochMillis: Long?, weakCategories: List<String>): List<StudyPlanDay>
}

data class TutorMessage(val fromUser: Boolean, val text: String)

/** Conversational help scoped to CDL exam topics. */
interface PersonalTutor {
    suspend fun ask(conversation: List<TutorMessage>, question: String): String
}

/** Suggests the next best questions for a user based on performance history. */
interface QuestionRecommendationEngine {
    suspend fun recommendNext(weakCategories: List<String>, limit: Int): List<Question>
}
