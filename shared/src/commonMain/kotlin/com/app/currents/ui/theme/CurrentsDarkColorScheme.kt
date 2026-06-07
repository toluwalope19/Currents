package com.app.currents.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val CurrentsDarkColorScheme = darkColorScheme(
    primary          = Accent,
    onPrimary        = DarkText,
    background       = DarkBackground,
    onBackground     = DarkText,
    surface          = DarkSurface,
    onSurface        = DarkText,
    surfaceVariant   = DarkSurface2,
    onSurfaceVariant = DarkText2,
    surfaceContainer = DarkSurface3,
    outline          = DarkBorder,
    secondary        = Accent,
    onSecondary      = DarkText,
)

val CurrentsLightColorScheme = lightColorScheme(
    primary          = Accent,
    onPrimary        = LightText,
    background       = LightBackground,
    onBackground     = LightText,
    surface          = LightSurface,
    onSurface        = LightText,
    surfaceVariant   = LightSurface2,
    onSurfaceVariant = LightText2,
    surfaceContainer = LightSurface3,
    outline          = LightBorder,
    secondary        = Accent,
    onSecondary      = LightText,
)