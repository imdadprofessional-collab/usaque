package com.cdlpermitprep.usa.data.firebase.model

/** Slim cloud-sync payloads. The question bank itself never lives in Firestore. */
data class UserDocument(
    val displayName: String = "",
    val email: String? = null,
    val isPremium: Boolean = false,
    val premiumTier: String = "NONE",
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
    val selectedState: String = "General",
    val dailyGoal: Int = 20,
    val updatedAt: Long = System.currentTimeMillis(),
)

data class PurchaseHistoryDocument(
    val productId: String = "",
    val purchaseTimeMillis: Long = 0,
    val tier: String = "NONE",
)

data class ExamHistoryDocument(
    val state: String = "",
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val durationMs: Long = 0,
    val passed: Boolean = false,
    val completedAt: Long = 0,
)

data class BookmarkDocument(val questionId: Long = 0, val createdAt: Long = 0)
