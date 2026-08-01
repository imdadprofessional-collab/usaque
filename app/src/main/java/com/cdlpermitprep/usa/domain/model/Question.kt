package com.cdlpermitprep.usa.domain.model

data class Question(
    val id: Long,
    val state: String,
    val category: String,
    val subCategory: String,
    val difficulty: String,
    val question: String,
    val options: Map<AnswerOption, String>,
    val correctAnswer: AnswerOption,
    val explanation: String,
    val imageUrl: String?,
    val reference: String?,
    val tags: List<String>,
    val isPremium: Boolean,
)

enum class AnswerOption { A, B, C, D }
