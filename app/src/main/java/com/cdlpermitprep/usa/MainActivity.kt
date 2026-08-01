package com.cdlpermitprep.usa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.cdlpermitprep.usa.data.billing.BillingManager
import com.cdlpermitprep.usa.data.billing.CurrentActivityHolder
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.presentation.navigation.CdlNavGraph
import com.cdlpermitprep.usa.presentation.theme.CdlPermitPrepTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var currentActivityHolder: CurrentActivityHolder

    @Inject lateinit var billingManager: BillingManager

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentActivityHolder.set(this)
        billingManager.startConnection()

        setContent {
            val darkModePref by userPreferences.darkMode.collectAsState(initial = "system")
            val useDarkTheme = when (darkModePref) {
                "on" -> true
                "off" -> false
                else -> isSystemInDarkTheme()
            }
            CdlPermitPrepTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                CdlNavGraph(navController = navController)
            }
        }
    }
}
