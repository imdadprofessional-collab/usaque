package com.cdlpermitprep.usa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cdlpermitprep.usa.data.local.entity.ExamAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamAttemptDao {
    @Insert
    suspend fun insert(attempt: ExamAttemptEntity): Long

    @Query("SELECT * FROM exam_attempts ORDER BY completedAt DESC")
    fun history(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts ORDER BY completedAt DESC LIMIT 1")
    suspend fun latest(): ExamAttemptEntity?

    @Query("SELECT AVG(CASE WHEN passed THEN 100.0 ELSE 0 END) FROM exam_attempts")
    fun passRateFlow(): Flow<Double?>
}
