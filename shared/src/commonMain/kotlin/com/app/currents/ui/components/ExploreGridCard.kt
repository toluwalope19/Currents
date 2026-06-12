package com.app.currents.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.CategoryBreaking
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExploreGridCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
    slideOffset: Float = 0f,
) {
    val categoryColor = article.category.toColor()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (article.isBreaking) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = CategoryBreaking,
                        shape = RoundedCornerShape(12.dp),
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        // Outer breaking border
        if (article.isBreaking) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.6f),
                                Color(0xFF0A0A0A),
                            )
                        )
                    )
            )
            // Red border overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Transparent)
                    .then(
                        Modifier.clip(RoundedCornerShape(12.dp))
                    )
            )
        }

        // Background image or gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (article.isBreaking) {
                        Modifier.background(
                            Brush.linearGradient(
                                colors = listOf(
                                    CategoryBreaking.copy(alpha = 0.15f),
                                    Color(0xFF0A0A0A),
                                )
                            )
                        )
                    } else {
                        Modifier.background(
                            Brush.linearGradient(
                                colors = listOf(
                                    categoryColor.copy(alpha = 0.7f),
                                    categoryColor.copy(alpha = 0.2f),
                                    Color(0xFF0A0A0A),
                                )
                            )
                        )
                    }
                )
        ) {
            if (article.imageUrl != null) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                // Dark overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF0A0A0A).copy(alpha = 0.7f),
                                )
                            )
                        )
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
        ) {
            // Top row — category chip + live refresh button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                if (article.isBreaking) {
                    BreakingBadge()
                } else {
                    CategoryChip(category = article.category)
                }

                if (isLive) {
                    LiveRefreshButton()
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Headline
            Text(
                text = article.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = if (isLive) {
                    Modifier.graphicsLayer {
                        translationY = slideOffset
                        alpha = 1f - (kotlin.math.abs(slideOffset) / 30f)
                    }
                } else Modifier,
            )
        }
    }
}

@Composable
private fun LiveRefreshButton() {
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(CurrentsIcons.Refresh),
            contentDescription = "Live refresh",
            tint = Color.White,
            modifier = Modifier
                .size(14.dp)
                .rotate(rotation),
        )
    }
}

@Preview
@Composable
private fun ExploreGridCardDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ExploreGridCard(
            isLive = true,
            article = Article(
                id = "1",
                title = "Chipmaker doubles AI output amid global demand",
                description = "",
                content = null,
                url = "",
                imageUrl = null,
                source = "Reuters",
                author = null,
                category = Category.Technology,
                publishedAt = "2h ago",
                isBookmarked = false,
            ),
            onClick = {},
            modifier = Modifier.size(180.dp, 160.dp),
        )
    }
}

@Preview
@Composable
private fun ExploreGridCardBreakingDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ExploreGridCard(
            article = Article(
                id = "2",
                title = "Evacuations ordered as storm makes landfall",
                description = "",
                content = null,
                url = "",
                imageUrl = null,
                source = "AP",
                author = null,
                category = Category.World,
                publishedAt = "5m ago",
                isBookmarked = false,
                isBreaking = true,
            ),
            onClick = {},
            modifier = Modifier.size(180.dp, 160.dp),
        )
    }
}