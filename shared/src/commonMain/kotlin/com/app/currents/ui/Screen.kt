package com.app.currents.ui

import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    data object Splash     : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home      : Screen("home")
    data object Explore   : Screen("explore")
    data object Search    : Screen("search")
    data object Bookmarks : Screen("bookmarks")
    data object Profile   : Screen("profile")
    data object Article : Screen("article/{articleId}?articleJson={articleJson}") {
        fun createRoute(articleId: String) = "article/$articleId"

        fun createRoute(article: com.app.currents.domain.model.Article): String {
            val json = Json.encodeToString(com.app.currents.domain.model.Article.serializer(), article).encodeURLParameter()
            return "article/${article.id}?articleJson=$json"
        }
    }

}