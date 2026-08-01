package com.cdlpermitprep.usa.domain.model

data class UserProfile(
    val uid: String?,
    val displayName: String,
    val email: String?,
    val isPremium: Boolean,
    val premiumTier: PremiumTier,
    val xp: Int,
    val level: Int,
    val streakDays: Int,
)

enum class PremiumTier { NONE, MONTHLY, YEARLY, LIFETIME }

data class PurchaseHistoryItem(
    val productId: String,
    val purchaseTimeMillis: Long,
    val tier: PremiumTier,
)

data class GamificationSnapshot(
    val xp: Int,
    val level: Int,
    val streakDays: Int,
    val xpToNextLevel: Int,
)
