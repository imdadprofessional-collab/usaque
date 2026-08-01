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
    val packId: String?,
)

enum class AnswerOption { A, B, C, D }

/** Single source of truth for "can this user see this question" — used by every path that
 * surfaces questions (random practice, mock exam, search) so premium-pack content never leaks
 * to a user who hasn't unlocked it. */
fun Question.isAccessibleWith(isPremiumSubscriber: Boolean, ownedPackIds: Set<String>): Boolean =
    !isPremium || isPremiumSubscriber || (packId != null && packId in ownedPackIds)
