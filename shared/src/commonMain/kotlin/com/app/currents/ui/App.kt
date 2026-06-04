package com.app.currents.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    MaterialTheme {
        Surface {
            AppNavHost()
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            PlaceholderScreen("Home")
        }
        composable(Screen.Explore.route) {
            PlaceholderScreen("Explore")
        }
        composable(Screen.Search.route) {
            PlaceholderScreen("Search")
        }
        composable(Screen.Bookmarks.route) {
            PlaceholderScreen("Bookmarks")
        }
        composable(Screen.Profile.route) {
            PlaceholderScreen("Profile")
        }
        composable(Screen.Article.route) {
            PlaceholderScreen("Article")
        }
    }
}