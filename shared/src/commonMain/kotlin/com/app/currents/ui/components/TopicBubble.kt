package com.app.currents.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopicBubble(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val categoryColor = category.toColor()

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Accent else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderColor",
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "scale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size),
    ) {
        // Main bubble
        Box(
            modifier = Modifier
                .size(size)
                .scale(scale)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Accent else categoryColor.copy(alpha = 0.4f),
                    shape = CircleShape,
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.4f),
                            categoryColor.copy(alpha = 0.15f),
                            Color(0xFF0D0D12),
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = category.label,
                color = Color.White,
                fontSize = (size.value * 0.14f).sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }

        // Checkmark badge — top right when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .clip(CircleShape)
                    .background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(CurrentsIcons.Check),
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopicBubbleSelectedDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        TopicBubble(
            category = Category.Technology,
            isSelected = true,
            onClick = {},
            size = 100.dp,
        )
    }
}

@Preview
@Composable
private fun TopicBubbleUnselectedDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        TopicBubble(
            category = Category.Sports,
            isSelected = false,
            onClick = {},
            size = 80.dp,
        )
    }
}

@Preview
@Composable
private fun TopicBubbleLightPreview() {
    CurrentsTheme(darkTheme = false) {
        TopicBubble(
            category = Category.Business,
            isSelected = false,
            onClick = {},
            size = 90.dp,
        )
    }
}