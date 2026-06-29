package com.app.currents.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.app.currents.domain.model.Article
import com.app.currents.presentation.splash.SplashViewModel
import com.app.currents.ui.bookmarks.BookmarksScreen
import io.ktor.http.decodeURLQueryComponent
import com.app.currents.ui.components.CurrentsNavBar
import com.app.currents.ui.components.NavTab
import com.app.currents.ui.profile.ProfileScreen
import com.app.currents.ui.screens.article.ArticleDetailScreen
import com.app.currents.ui.screens.explore.ExploreScreen
import com.app.currents.ui.screens.home.HomeScreen
import com.app.currents.ui.screens.onboarding.OnboardingScreen
import com.app.currents.ui.screens.search.SearchScreen
import com.app.currents.ui.screens.splash.SplashScreen
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.util.SystemBars
import com.app.currents.util.openUrl
import com.app.currents.util.shareText
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    var darkTheme by remember { mutableStateOf(true) }

    CurrentsTheme(darkTheme = darkTheme) {
        SystemBars(darkTheme = darkTheme)
        AppNavHost(
            darkTheme = darkTheme,
            onThemeToggle = { darkTheme = !darkTheme },
        )
    }
}


@Composable
fun AppNavHost(
    darkTheme: Boolean,
    onThemeToggle: () -> Unit,
){
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(NavTab.Home) }
    val splashViewModel = koinViewModel<SplashViewModel>()
    val isOnboardingComplete by splashViewModel.isOnboardingComplete.collectAsStateWithLifecycle()


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showNavBar = currentRoute != null && currentRoute in listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Search.route,
        Screen.Bookmarks.route,
        Screen.Profile.route,
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showNavBar) {
                CurrentsNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        val route = when (tab) {
                            NavTab.Home      -> Screen.Home.route
                            NavTab.Explore   -> Screen.Explore.route
                            NavTab.Search    -> Screen.Search.route
                            NavTab.Bookmarks -> Screen.Bookmarks.route
                            NavTab.Profile   -> Screen.Profile.route
                        }
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        isOnboardingComplete = isOnboardingComplete,
                        onSplashComplete = { showOnboarding ->
                            val destination = if (showOnboarding) Screen.Onboarding.route
                            else Screen.Home.route
                            navController.navigate(destination) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                    )
                }
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onContinue = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        },
                        onSkip = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        },
                    )
                }
                composable(Screen.Home.route) {
                    HomeScreen(
                        isDarkTheme = darkTheme,
                        onThemeToggle = onThemeToggle,
                        onArticleClick = { article ->
                            navController.navigate(Screen.Article.createRoute(article.id))
                        },
                    )
                }
                composable(Screen.Explore.route) {
                    ExploreScreen(
                        onArticleClick = { article ->
                            navController.navigate(Screen.Article.createRoute(article.id))
                        },
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onArticleClick = { article ->
                            navController.navigate(Screen.Article.createRoute(article))
                        },
                    )
                }
                composable(Screen.Bookmarks.route) {
                    BookmarksScreen(
                        onArticleClick = { article ->
                            navController.navigate(Screen.Article.createRoute(article.id))
                        },
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        isDarkTheme = darkTheme,
                        onThemeToggle = onThemeToggle,
                        onSignOut = {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    )

                }
                composable(
                    route = Screen.Article.route,
                    arguments = listOf(
                        navArgument("articleId") { type = NavType.StringType },
                        navArgument("articleJson") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                    ),
                ) { backStackEntry ->
                    val articleId = backStackEntry.arguments?.read { getString("articleId") }
                        ?: return@composable
                    val articleJson = try {
                        backStackEntry.arguments?.read { getString("articleJson") }
                    } catch (_: Exception) { null }
                    val article = articleJson?.let {
                        Json.decodeFromString(Article.serializer(), it.decodeURLQueryComponent())
                    }
                    ArticleDetailScreen(
                        articleId = articleId,
                        article = article,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

