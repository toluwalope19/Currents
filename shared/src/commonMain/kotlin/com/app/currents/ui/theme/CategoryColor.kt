package com.app.currents.ui.theme

import androidx.compose.ui.graphics.Color
import com.app.currents.domain.model.Category


fun Category.toColor(): Color = when (this) {
    Category.Top           -> CategoryTop
    Category.Technology    -> CategoryTech
    Category.Business      -> CategoryBusiness
    Category.Sports        -> CategorySports
    Category.Health        -> CategoryHealth
    Category.Science       -> CategoryScience
    Category.Entertainment -> CategoryEntertainment
    Category.Politics      -> CategoryPolitics
    Category.World         -> CategoryWorld
    Category.Food          -> CategoryFood
    Category.Environment   -> CategoryEnvironment
    Category.Lifestyle     -> CategoryLifestyle
    Category.Tourism       -> CategoryTourism
    Category.Crime         -> CategoryCrime
    Category.Education     -> CategoryEducation
    Category.Domestic      -> CategoryDomestic
    Category.Other         -> CategoryOther
    Category.Culture       -> CategoryCulture
    Category.Art           -> CategoryArt
    Category.Music         -> CategoryMusic
    Category.Gaming        -> CategoryGaming
    is Category.Unknown    -> CategoryTop
}

fun Category.toBackgroundColor(): Color = toColor().copy(alpha = 0.15f)