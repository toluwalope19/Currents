package com.app.currents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import org.jetbrains.compose.resources.painterResource

data class StoryItemData(
    val id: String,
    val label: String,
    val gradientColors: List<Color>,
    val isForYou: Boolean = false,
)

@Composable
fun StoryItem(
    data: StoryItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (data.isForYou) Accent
                    else Accent.copy(alpha = 0.5f),
                    shape = CircleShape,
                )
                .then(
                    if (data.isForYou) {
                        Modifier.background(Color.Transparent)
                    } else {
                        Modifier.background(Brush.radialGradient(data.gradientColors))
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (data.isForYou) {
                Icon(
                    painter = painterResource(CurrentsIcons.Spark),
                    contentDescription = "For You",
                    tint = Accent,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Text(
            text = data.label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun StoryItemForYouDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        StoryItem(
            data = StoryItemData(
                id = "foryou",
                label = "For You",
                gradientColors = listOf(Color.Transparent, Color.Transparent),
                isForYou = true,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun StoryItemTopicDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        StoryItem(
            data = StoryItemData(
                id = "world",
                label = "World",
                gradientColors = listOf(
                    Color(0xFF3FB7FF).copy(alpha = 0.8f),
                    Color(0xFF0A0A0A),
                ),
            ),
            onClick = {},
        )
    }
}