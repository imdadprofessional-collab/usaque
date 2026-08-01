package com.cdlpermitprep.usa.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** One row per question a user has ever answered; used to drive weak-topic and wrong-answer views. */
@Entity(
    tableName = "answer_records",
    indices = [Index("questionId"), Index("isCorrect"), Index("category")],
)
data class AnswerRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val category: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val timeSpentMs: Long,
    val mode: String, // PRACTICE, MOCK_EXAM, DAILY_CHALLENGE
    val answeredAt: Long = System.currentTimeMillis(),
)
