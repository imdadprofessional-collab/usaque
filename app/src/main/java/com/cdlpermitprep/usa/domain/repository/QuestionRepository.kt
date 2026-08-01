package com.cdlpermitprep.usa.domain.repository

import androidx.paging.PagingData
import com.cdlpermitprep.usa.domain.model.Question
import kotlinx.coroutines.flow.Flow

data class QuestionFilter(
    val state: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
    val keyword: String? = null,
)

interface QuestionRepository {
    fun pagedQuestions(filter: QuestionFilter): Flow<PagingData<Question>>
    fun bookmarkedQuestions(): Flow<PagingData<Question>>
    fun wrongAnswerQuestions(): Flow<PagingData<Question>>
    suspend fun getQuestionById(id: Long): Question?
    suspend fun randomQuestions(state: String?, category: String?, limit: Int): List<Question>
    suspend fun getCategories(): List<String>
    suspend fun getStates(): List<String>
}
