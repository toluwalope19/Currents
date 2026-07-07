package com.app.currents.wear.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.app.currents.wear.presentation.feed.FeedScreen
import com.app.currents.wear.presentation.article.ArticleScreen
import com.app.currents.wear.presentation.saved.SavedScreen

sealed class WearScreen(val route: String) {
    data object Feed    : WearScreen("feed")
    data object Article : WearScreen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }
    data object Saved   : WearScreen("saved")
}

@Composable
fun WearNavGraph() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = WearScreen.Feed.route,
    ) {
        composable(WearScreen.Feed.route) {
            FeedScreen(
                onArticleClick = { articleId ->
                    navController.navigate(WearScreen.Article.createRoute(articleId))
                },
                onSavedClick = {
                    navController.navigate(WearScreen.Saved.route)
                },
            )
        }
        composable(WearScreen.Article.route) { backStack ->
            val articleId = backStack.arguments?.getString("articleId") ?: return@composable
            ArticleScreen(
                articleId = articleId,
                onBack = { navController.popBackStack() },
            )
        }
        composable(WearScreen.Saved.route) {
            SavedScreen(
                onArticleClick = { articleId ->
                    navController.navigate(WearScreen.Article.createRoute(articleId))
                },
            )
        }
    }
}