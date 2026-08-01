package com.cdlpermitprep.usa.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cdlpermitprep.usa.data.local.dao.QuestionDao
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.QuestionFilter
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private const val PAGE_SIZE = 20

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
) : QuestionRepository {

    override fun pagedQuestions(filter: QuestionFilter): Flow<PagingData<Question>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            questionDao.pagingSource(
                state = filter.state,
                category = filter.category,
                difficulty = filter.difficulty,
                keyword = filter.keyword,
            )
        }.flow.map { paging -> paging.map { it.toDomain() } }

    override fun bookmarkedQuestions(): Flow<PagingData<Question>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            questionDao.bookmarkedQuestionsPagingSource()
        }.flow.map { paging -> paging.map { it.toDomain() } }

    override fun wrongAnswerQuestions(): Flow<PagingData<Question>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            questionDao.wrongAnswersPagingSource()
        }.flow.map { paging -> paging.map { it.toDomain() } }

    override suspend fun getQuestionById(id: Long): Question? = questionDao.getById(id)?.toDomain()

    override suspend fun randomQuestions(state: String?, category: String?, limit: Int): List<Question> =
        questionDao.randomQuestions(state, category, limit).map { it.toDomain() }

    override suspend fun getCategories(): List<String> = questionDao.getCategories()

    override suspend fun getStates(): List<String> = questionDao.getStates()
}
