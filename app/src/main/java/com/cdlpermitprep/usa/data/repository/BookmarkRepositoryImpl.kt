package com.cdlpermitprep.usa.data.repository

import com.cdlpermitprep.usa.data.local.dao.BookmarkDao
import com.cdlpermitprep.usa.data.local.entity.BookmarkEntity
import com.cdlpermitprep.usa.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
) : BookmarkRepository {

    override suspend fun toggleBookmark(questionId: Long, isBookmarked: Boolean) {
        if (isBookmarked) {
            bookmarkDao.insert(BookmarkEntity(questionId = questionId))
        } else {
            bookmarkDao.deleteByQuestionId(questionId)
        }
    }

    override fun isBookmarked(questionId: Long): Flow<Boolean> = bookmarkDao.isBookmarked(questionId)

    override fun bookmarkCount(): Flow<Int> = bookmarkDao.countFlow()
}
