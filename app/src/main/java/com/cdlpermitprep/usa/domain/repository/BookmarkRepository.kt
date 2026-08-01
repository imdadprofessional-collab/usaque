package com.cdlpermitprep.usa.domain.repository

import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun toggleBookmark(questionId: Long, isBookmarked: Boolean)
    fun isBookmarked(questionId: Long): Flow<Boolean>
    fun bookmarkCount(): Flow<Int>
}
