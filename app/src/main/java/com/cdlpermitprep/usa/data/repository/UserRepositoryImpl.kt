package com.cdlpermitprep.usa.data.repository

import com.cdlpermitprep.usa.data.firebase.model.UserDocument
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.domain.model.GamificationSnapshot
import com.cdlpermitprep.usa.domain.model.PremiumTier
import com.cdlpermitprep.usa.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cdlpermitprep.usa.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences,
) : UserRepository {

    private fun usersCollection() = firestore.collection("users")

    override fun currentUser(): Flow<UserProfile?> = combine(
        userPreferences.userName,
        userPreferences.isPremium,
        userPreferences.gamification,
    ) { name, isPremium, gamification ->
        UserProfile(
            uid = auth.currentUser?.uid,
            displayName = name,
            email = auth.currentUser?.email,
            isPremium = isPremium,
            premiumTier = if (isPremium) PremiumTier.MONTHLY else PremiumTier.NONE,
            xp = gamification.xp,
            level = gamification.level,
            streakDays = gamification.streakDays,
        )
    }

    override suspend fun signInAnonymously(): Result<Unit> = runCatching {
        auth.signInAnonymously().await()
        Unit
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    override suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        userPreferences.setUserName(displayName)
        Unit
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun syncToCloud() {
        val uid = auth.currentUser?.uid ?: return
        runCatching {
            val gamification = userPreferences.gamification
            val doc = UserDocument(
                displayName = auth.currentUser?.displayName.orEmpty(),
                email = auth.currentUser?.email,
                isPremium = false,
            )
            usersCollection().document(uid).set(doc).await()
        }.onFailure { Timber.w(it, "Cloud sync skipped (offline or error)") }
    }

    override fun gamification(): Flow<GamificationSnapshot> = userPreferences.gamification.map {
        GamificationSnapshot(
            xp = it.xp,
            level = it.level,
            streakDays = it.streakDays,
            xpToNextLevel = 500 - (it.xp % 500),
        )
    }

    override suspend fun awardXp(amount: Int) {
        userPreferences.addXp(amount)
    }

    override suspend fun recordStudySession() {
        val epochDay = LocalDate.now().toEpochDay()
        userPreferences.recordStudyDayAndUpdateStreak(epochDay)
    }
}
