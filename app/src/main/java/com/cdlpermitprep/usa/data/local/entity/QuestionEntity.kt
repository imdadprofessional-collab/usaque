package com.cdlpermitprep.usa.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single exam question. Indexed on the fields users filter/search by so lookups
 * stay fast even when the bank grows into the tens/hundreds of thousands of rows.
 */
@Entity(
    tableName = "questions",
    indices = [
        Index("state"),
        Index("category"),
        Index("subCategory"),
        Index("difficulty"),
        Index("packId"),
        Index(value = ["state", "category"]),
    ],
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val state: String,
    val category: String,
    val subCategory: String,
    val difficulty: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val explanation: String,
    val imageUrl: String? = null,
    val reference: String? = null,
    val tags: String = "",
    val isPremium: Boolean = false,
    /** Non-null for content gated behind an individually purchasable pack (see [com.cdlpermitprep.usa.data.billing.BillingProducts]); null means either free, or unlocked by any full premium subscription/lifetime purchase when [isPremium] is true. */
    val packId: String? = null,
)
