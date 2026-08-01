package com.cdlpermitprep.usa.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cdlpermitprep.usa.presentation.analytics.AnalyticsScreen
import com.cdlpermitprep.usa.presentation.auth.LoginScreen
import com.cdlpermitprep.usa.presentation.bookmarks.BookmarksScreen
import com.cdlpermitprep.usa.presentation.category.CategoryScreen
import com.cdlpermitprep.usa.presentation.home.HomeScreen
import com.cdlpermitprep.usa.presentation.mockexam.MockExamScreen
import com.cdlpermitprep.usa.presentation.onboarding.OnboardingScreen
import com.cdlpermitprep.usa.presentation.practice.PracticeScreen
import com.cdlpermitprep.usa.presentation.premium.PremiumScreen
import com.cdlpermitprep.usa.presentation.profile.ProfileScreen
import com.cdlpermitprep.usa.presentation.result.ResultScreen
import com.cdlpermitprep.usa.presentation.search.SearchScreen
import com.cdlpermitprep.usa.presentation.settings.SettingsScreen
import com.cdlpermitprep.usa.presentation.splash.SplashScreen

@Composable
fun CdlNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(onNavigateNext = { start ->
                navController.navigate(start) { popUpTo(Screen.Splash.route) { inclusive = true } }
            })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(onLoggedIn = {
                navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onOpenCategory = { name -> navController.navigate(Screen.Category.createRoute(name)) },
                onOpenSearch = { navController.navigate(Screen.Search.route) },
                onOpenBookmarks = { navController.navigate(Screen.Bookmarks.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenMockExam = { navController.navigate(Screen.MockExam.route) },
                onOpenAnalytics = { navController.navigate(Screen.Analytics.route) },
                onOpenPremium = { navController.navigate(Screen.Premium.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName").orEmpty()
            CategoryScreen(
                categoryName = categoryName,
                onBack = { navController.popBackStack() },
                onStartPractice = { mode, category ->
                    navController.navigate(Screen.Practice.createRoute(mode.name, category))
                },
            )
        }

        composable(
            route = Screen.Practice.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType },
            ),
        ) {
            PracticeScreen(
                onBack = { navController.popBackStack() },
                onFinished = { _, _ -> navController.popBackStack() },
            )
        }

        composable(Screen.MockExam.route) {
            MockExamScreen(
                onBack = { navController.popBackStack() },
                onExamSubmitted = { attemptId ->
                    navController.navigate(Screen.Result.createRoute(attemptId)) {
                        popUpTo(Screen.MockExam.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.LongType }),
        ) {
            ResultScreen(onDone = {
                navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
            })
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenPremium = { navController.navigate(Screen.Premium.route) },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Premium.route) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Search.route) {
            SearchScreen(onBack = { navController.popBackStack() })
        }
    }
}
