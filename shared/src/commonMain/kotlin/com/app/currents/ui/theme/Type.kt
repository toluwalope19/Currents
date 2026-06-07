package com.app.currents.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CurrentsTypography = Typography(
    // App name — 32sp / w800
    displayLarge = TextStyle(
        fontSize   = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.6).sp,
    ),
    // Screen title — 22sp / w700
    headlineMedium = TextStyle(
        fontSize   = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
    ),
    // Card headline — 20sp / w800
    headlineSmall = TextStyle(
        fontSize   = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.3).sp,
    ),
    // Section header — 17sp / w700
    titleLarge = TextStyle(
        fontSize   = 17.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
    ),
    // List headline — 15sp / w600
    titleMedium = TextStyle(
        fontSize   = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
    ),
    // Body — 15sp / w400
    bodyLarge = TextStyle(
        fontSize   = 15.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
    ),
    // Label — 11sp / w600 / ALL CAPS / tracking 0.8
    labelSmall = TextStyle(
        fontSize   = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
    ),
)