package com.cdlpermitprep.usa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val state: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val durationMs: Long,
    val passed: Boolean,
    val startedAt: Long,
    val completedAt: Long,
)
