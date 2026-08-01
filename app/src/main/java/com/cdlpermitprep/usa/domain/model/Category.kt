package com.cdlpermitprep.usa.domain.model

data class Category(
    val name: String,
    val questionCount: Int,
    val isPremium: Boolean = false,
)

enum class PracticeMode {
    TOPIC_WISE, RANDOM, WEAK_TOPICS, WRONG_ANSWERS, BOOKMARKED, DAILY_CHALLENGE, UNLIMITED, TIMED,
}
