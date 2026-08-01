package com.cdlpermitprep.usa.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "cdl_user_prefs")

data class GamificationState(
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
    val lastStudyDayEpoch: Long = 0L,
)

/** Local-first source of truth for settings/streak/premium flag; synced to Firestore when online. */
@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val SELECTED_STATE = stringPreferencesKey("selected_state")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "system" | "on" | "off"
        val DAILY_GOAL = intPreferencesKey("daily_goal_questions")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val EXAM_DATE_EPOCH = longPreferencesKey("exam_date_epoch")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val XP = intPreferencesKey("xp")
        val LEVEL = intPreferencesKey("level")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_STUDY_DAY = longPreferencesKey("last_study_day")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val selectedState: Flow<String> = context.dataStore.data.map { it[Keys.SELECTED_STATE] ?: "General" }
    val darkMode: Flow<String> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: "system" }
    val dailyGoal: Flow<Int> = context.dataStore.data.map { it[Keys.DAILY_GOAL] ?: 20 }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    val examDateEpoch: Flow<Long?> = context.dataStore.data.map { it[Keys.EXAM_DATE_EPOCH] }
    val isPremium: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_PREMIUM] ?: false }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "Driver" }

    val gamification: Flow<GamificationState> = context.dataStore.data.map {
        GamificationState(
            xp = it[Keys.XP] ?: 0,
            level = it[Keys.LEVEL] ?: 1,
            streakDays = it[Keys.STREAK_DAYS] ?: 0,
            lastStudyDayEpoch = it[Keys.LAST_STUDY_DAY] ?: 0L,
        )
    }

    suspend fun setSelectedState(state: String) = context.dataStore.edit { it[Keys.SELECTED_STATE] = state }
    suspend fun setDarkMode(mode: String) = context.dataStore.edit { it[Keys.DARK_MODE] = mode }
    suspend fun setDailyGoal(goal: Int) = context.dataStore.edit { it[Keys.DAILY_GOAL] = goal }
    suspend fun setNotificationsEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    suspend fun setExamDate(epochMillis: Long) = context.dataStore.edit { it[Keys.EXAM_DATE_EPOCH] = epochMillis }
    suspend fun setPremium(isPremium: Boolean) = context.dataStore.edit { it[Keys.IS_PREMIUM] = isPremium }
    suspend fun setOnboardingDone(done: Boolean) = context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    suspend fun setUserName(name: String) = context.dataStore.edit { it[Keys.USER_NAME] = name }

    suspend fun addXp(amount: Int) = context.dataStore.edit { prefs ->
        val newXp = (prefs[Keys.XP] ?: 0) + amount
        prefs[Keys.XP] = newXp
        prefs[Keys.LEVEL] = 1 + newXp / 500 // 500xp per level
    }

    suspend fun recordStudyDayAndUpdateStreak(todayEpochDay: Long) = context.dataStore.edit { prefs ->
        val lastDay = prefs[Keys.LAST_STUDY_DAY] ?: 0L
        val currentStreak = prefs[Keys.STREAK_DAYS] ?: 0
        prefs[Keys.STREAK_DAYS] = when (todayEpochDay - lastDay) {
            0L -> currentStreak
            1L -> currentStreak + 1
            else -> 1
        }
        prefs[Keys.LAST_STUDY_DAY] = todayEpochDay
    }
}
