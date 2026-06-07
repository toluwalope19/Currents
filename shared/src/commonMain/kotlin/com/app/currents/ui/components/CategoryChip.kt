package com.app.currents.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val categoryColor = category.toColor()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(categoryColor.copy(alpha = 0.15f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = category.label.uppercase(),
            color = categoryColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}

@Preview
@Composable
private fun CategoryPillSelectedDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        CategoryChip(
            category = Category.Technology,
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun CategoryPillUnselectedDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        CategoryChip(
            category = Category.Sports,
            isSelected = false,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun CategoryPillSelectedLightPreview() {
    CurrentsTheme(darkTheme = false) {
        CategoryChip(
            category = Category.Health,
            isSelected = true,
            onClick = {},
        )
    }
}