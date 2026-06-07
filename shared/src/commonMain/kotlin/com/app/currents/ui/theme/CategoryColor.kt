package com.app.currents.ui.theme

import androidx.compose.ui.graphics.Color
import com.app.currents.domain.model.Category

fun Category.toColor(): Color = when (this) {
    Category.Technology    -> CategoryTech
    Category.Sports        -> CategorySports
    Category.Health        -> CategoryHealth
    Category.Business      -> CategoryBusiness
    Category.Science       -> CategoryScience
    Category.Entertainment -> CategoryEntertainment
    Category.Politics      -> CategoryPolitics
    Category.World         -> CategoryWorld
    Category.Top           -> Accent
    is Category.Unknown    -> Accent
}

fun Category.toBackgroundColor(): Color = toColor().copy(alpha = 0.15f)