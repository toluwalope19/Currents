package com.app.currents.wear.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import com.app.currents.ui.theme.CurrentsTheme

@Composable
fun WearApp() {
    CurrentsTheme() {
        WearNavGraph()
    }
}