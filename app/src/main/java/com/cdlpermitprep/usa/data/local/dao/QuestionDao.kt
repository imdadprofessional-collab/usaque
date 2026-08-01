package com.cdlpermitprep.usa.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cdlpermitprep.usa.data.local.entity.QuestionEntity

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    /**
     * Paged, filterable query. Never materializes the whole table — PagingSource
     * streams pages from Room/SQLite so the bank can scale to 100k+ rows.
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE (:state IS NULL OR state = :state OR state = 'General')
        AND (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:keyword IS NULL OR question LIKE '%' || :keyword || '%' OR tags LIKE '%' || :keyword || '%')
        ORDER BY id ASC
        """
    )
    fun pagingSource(
        state: String?,
        category: String?,
        difficulty: String?,
        keyword: String?,
    ): PagingSource<Int, QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: Long): QuestionEntity?

    @Query(
        """
        SELECT * FROM questions
        WHERE (:state IS NULL OR state = :state OR state = 'General')
        AND (:category IS NULL OR category = :category)
        ORDER BY RANDOM() LIMIT :limit
        """
    )
    suspend fun randomQuestions(state: String?, category: String?, limit: Int): List<QuestionEntity>

    @Query("SELECT DISTINCT category FROM questions ORDER BY category ASC")
    suspend fun getCategories(): List<String>

    @Query("SELECT DISTINCT state FROM questions ORDER BY state ASC")
    suspend fun getStates(): List<String>

    @Query(
        """
        SELECT q.* FROM questions q
        INNER JOIN bookmarks b ON b.questionId = q.id
        ORDER BY b.createdAt DESC
        """
    )
    fun bookmarkedQuestionsPagingSource(): PagingSource<Int, QuestionEntity>

    @Query(
        """
        SELECT q.* FROM questions q
        INNER JOIN (
            SELECT questionId, MAX(answeredAt) as lastAnswered
            FROM answer_records WHERE isCorrect = 0
            GROUP BY questionId
        ) w ON w.questionId = q.id
        ORDER BY w.lastAnswered DESC
        """
    )
    fun wrongAnswersPagingSource(): PagingSource<Int, QuestionEntity>
}
