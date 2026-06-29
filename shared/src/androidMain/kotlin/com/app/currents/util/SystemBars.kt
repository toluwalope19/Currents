package com.app.currents.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
actual fun SystemBars(darkTheme: Boolean) {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = MaterialTheme.colorScheme.background
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = !darkTheme,
        )
    }
}