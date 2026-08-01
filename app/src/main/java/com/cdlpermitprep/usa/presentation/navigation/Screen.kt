package com.cdlpermitprep.usa.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    data object Practice : Screen("practice/{mode}/{categoryName}") {
        fun createRoute(mode: String, categoryName: String = "all") = "practice/$mode/$categoryName"
    }
    data object MockExam : Screen("mock_exam")
    data object Result : Screen("result/{attemptId}") {
        fun createRoute(attemptId: Long) = "result/$attemptId"
    }
    data object Analytics : Screen("analytics")
    data object Bookmarks : Screen("bookmarks")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Premium : Screen("premium")
    data object Search : Screen("search")
}
