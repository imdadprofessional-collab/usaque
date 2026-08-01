package com.cdlpermitprep.usa.presentation.splash

import androidx.lifecycle.ViewModel
import com.cdlpermitprep.usa.data.local.DatabaseSeeder
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val databaseSeeder: DatabaseSeeder,
    private val externalScope: kotlinx.coroutines.CoroutineScope,
) : ViewModel() {

    init {
        databaseSeeder.seedIfEmpty(externalScope)
    }

    suspend fun resolveStartDestination(): String {
        val onboardingDone = userPreferences.onboardingDone.first()
        return if (onboardingDone) Screen.Home.route else Screen.Onboarding.route
    }
}
