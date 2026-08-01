package com.cdlpermitprep.usa.domain.model

data class Category(
    val name: String,
    val questionCount: Int,
    val isPremium: Boolean = false,
    /** The specific pack that unlocks this category on its own, if any (see [com.cdlpermitprep.usa.data.billing.BillingProducts]). */
    val packId: String? = null,
)

enum class PracticeMode {
    TOPIC_WISE, RANDOM, WEAK_TOPICS, WRONG_ANSWERS, BOOKMARKED, DAILY_CHALLENGE, UNLIMITED, TIMED,
}

/**
 * Maps category names to the individually-purchasable pack that unlocks them (see
 * [com.cdlpermitprep.usa.data.billing.BillingProducts.PREMIUM_PACKS]). A category not listed
 * here is free. Any full subscription/lifetime purchase unlocks all categories regardless of
 * this mapping.
 */
object CategoryPacks {
    val MAP: Map<String, String> = mapOf(
        "Hazardous Materials" to "pack_hazmat",
        "Tank Vehicles" to "pack_tanker",
        "Doubles/Triples" to "pack_doubles_triples",
        "Passenger Vehicles" to "pack_passenger",
        "School Bus" to "pack_passenger",
    )

    fun packFor(categoryName: String): String? = MAP[categoryName]
    fun isPremiumCategory(categoryName: String): Boolean = MAP.containsKey(categoryName)
}
