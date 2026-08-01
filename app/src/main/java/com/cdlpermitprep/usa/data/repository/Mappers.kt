package com.cdlpermitprep.usa.data.repository

import com.cdlpermitprep.usa.data.local.entity.ExamAttemptEntity
import com.cdlpermitprep.usa.data.local.entity.QuestionEntity
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.domain.model.ExamAttempt
import com.cdlpermitprep.usa.domain.model.Question

fun QuestionEntity.toDomain(): Question = Question(
    id = id,
    state = state,
    category = category,
    subCategory = subCategory,
    difficulty = difficulty,
    question = question,
    options = mapOf(
        AnswerOption.A to optionA,
        AnswerOption.B to optionB,
        AnswerOption.C to optionC,
        AnswerOption.D to optionD,
    ),
    correctAnswer = AnswerOption.valueOf(correctAnswer),
    explanation = explanation,
    imageUrl = imageUrl,
    reference = reference,
    tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    isPremium = isPremium,
)

fun ExamAttemptEntity.toDomain(): ExamAttempt = ExamAttempt(
    id = id,
    state = state,
    totalQuestions = totalQuestions,
    correctCount = correctCount,
    durationMs = durationMs,
    passed = passed,
    startedAt = startedAt,
    completedAt = completedAt,
)

fun ExamAttempt.toEntity(): ExamAttemptEntity = ExamAttemptEntity(
    id = id,
    state = state,
    totalQuestions = totalQuestions,
    correctCount = correctCount,
    durationMs = durationMs,
    passed = passed,
    startedAt = startedAt,
    completedAt = completedAt,
)
