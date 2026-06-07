package com.app.currents.ui

sealed class Screen(val route: String) {
    data object Splash     : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home      : Screen("home")
    data object Explore   : Screen("explore")
    data object Search    : Screen("search")
    data object Bookmarks : Screen("bookmarks")
    data object Profile   : Screen("profile")
    data object Article   : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }
}