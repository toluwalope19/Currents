package com.app.currents.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.currents.domain.model.Category

@Composable
fun ScatteredBubbleLayout(
    categories: List<Category>,
    selectedCategories: List<Category>,
    onCategoryToggled: (Category) -> Unit,
) {
    // Size per category — mirrors design
    val sizes: Map<Category, Dp> = mapOf(
        Category.Technology to 110.dp,
        Category.Sports      to 110.dp,
        Category.Health      to 90.dp,
        Category.Business    to 85.dp,
        Category.Culture     to 75.dp,
        Category.Politics    to 75.dp,
        Category.Food        to 72.dp,
        Category.Science     to 68.dp,
        Category.Entertainment      to 65.dp,
        Category.Art         to 65.dp,
        Category.Music       to 68.dp,
        Category.Gaming      to 70.dp,
    )

    // Fractional x/y positions (0f = left/top, 1f = right/bottom)
    // These mirror the organic layout from the design
    val positions: Map<Category, Pair<Float, Float>> = mapOf(
        Category.Technology to Pair(0.05f, 0.0f),
        Category.Sports      to Pair(0.55f, 0.02f),
        Category.Health      to Pair(0.02f, 0.25f),
        Category.Business    to Pair(0.35f, 0.20f),
        Category.Culture     to Pair(0.68f, 0.22f),
        Category.Politics    to Pair(0.38f, 0.42f),
        Category.Food        to Pair(0.65f, 0.40f),
        Category.Science     to Pair(0.15f, 0.44f),
        Category.Entertainment      to Pair(0.02f, 0.60f),
        Category.Art         to Pair(0.30f, 0.62f),
        Category.Music       to Pair(0.52f, 0.60f),
        Category.Gaming      to Pair(0.70f, 0.58f),
    )

    Layout(
        content = {
            categories.forEach { category ->
                val size = sizes[category] ?: 72.dp
                TopicBubble(
                    category = category,
                    isSelected = selectedCategories.contains(category),
                    onClick = { onCategoryToggled(category) },
                    size = size,
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val placeables = measurables.mapIndexed { index, measurable ->
            val category = categories[index]
            val sizeDp = sizes[category] ?: 72.dp
            val sizePx = sizeDp.toPx().toInt()
            measurable.measure(
                constraints.copy(
                    minWidth = sizePx,
                    maxWidth = sizePx,
                    minHeight = sizePx,
                    maxHeight = sizePx,
                )
            )
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val category = categories[index]
                val (xFrac, yFrac) = positions[category] ?: Pair(0.5f, 0.5f)
                val x = (constraints.maxWidth * xFrac).toInt()
                val y = (constraints.maxHeight * yFrac).toInt()
                placeable.place(x, y)
            }
        }
    }
}