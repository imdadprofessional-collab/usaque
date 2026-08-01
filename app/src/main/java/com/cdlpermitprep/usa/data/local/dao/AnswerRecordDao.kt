package com.cdlpermitprep.usa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cdlpermitprep.usa.data.local.entity.AnswerRecordEntity
import kotlinx.coroutines.flow.Flow

data class CategoryAccuracy(val category: String, val correct: Int, val total: Int)
data class DailyActivity(val day: String, val answered: Int, val correct: Int)

@Dao
interface AnswerRecordDao {
    @Insert
    suspend fun insert(record: AnswerRecordEntity)

    @Query("SELECT COUNT(*) FROM answer_records")
    fun totalAnsweredFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM answer_records WHERE isCorrect = 1")
    fun totalCorrectFlow(): Flow<Int>

    @Query(
        """
        SELECT category, SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) as correct, COUNT(*) as total
        FROM answer_records GROUP BY category
        """
    )
    fun accuracyByCategory(): Flow<List<CategoryAccuracy>>

    @Query(
        """
        SELECT strftime('%Y-%m-%d', answeredAt / 1000, 'unixepoch') as day,
               COUNT(*) as answered,
               SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) as correct
        FROM answer_records
        WHERE answeredAt >= :sinceMillis
        GROUP BY day ORDER BY day ASC
        """
    )
    fun dailyActivitySince(sinceMillis: Long): Flow<List<DailyActivity>>

    @Query("SELECT AVG(timeSpentMs) FROM answer_records")
    fun averageTimeMsFlow(): Flow<Double?>

    @Query("SELECT DISTINCT questionId FROM answer_records WHERE isCorrect = 0")
    suspend fun wrongQuestionIds(): List<Long>
}
