package com.app.currents.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.currents.ui.theme.CurrentsTheme

@Composable
fun App() {
    var darkTheme by remember { mutableStateOf(true) }

    CurrentsTheme(darkTheme = darkTheme) {
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