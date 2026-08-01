package com.cdlpermitprep.usa.domain.repository

import com.cdlpermitprep.usa.domain.model.GamificationSnapshot
import com.cdlpermitprep.usa.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun currentUser(): Flow<UserProfile?>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<Unit>
    suspend fun signOut()

    /** Pushes local profile/progress/settings to Firestore; safe no-op when offline. */
    suspend fun syncToCloud()

    fun gamification(): Flow<GamificationSnapshot>
    suspend fun awardXp(amount: Int)
    suspend fun recordStudySession()
}
