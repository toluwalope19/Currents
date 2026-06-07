package com.app.currents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun ArticleHeroCard(
    article: Article,
    onArticleClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryColor = article.category.toColor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onArticleClick,
            ),
    ) {
        // Background image
        if (article.imageUrl != null) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Fallback gradient using category color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.8f),
                                Color(0xFF0A0A0A),
                            )
                        )
                    )
            )
        }

        // Dark gradient overlay — text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0A0A0A).copy(alpha = 0.3f),
                            Color(0xFF0A0A0A).copy(alpha = 0.85f),
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            // Top row — category chip + bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                CategoryChip(
                    category = article.category,
                    isSelected = false,
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBookmarkClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            if (article.isBookmarked) CurrentsIcons.BookmarkFill
                            else CurrentsIcons.Bookmark
                        ),
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) Accent else Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom — headline + source
            Text(
                text = article.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                lineHeight = 26.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${article.source} · ${article.publishedAt}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ArticleHeroCardDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ArticleHeroCard(
            article = Article(
                id = "1",
                title = "Global leaders reach historic accord at climate summit",
                description = "Nearly 200 nations agreed to triple renewable capacity",
                content = null,
                url = "",
                imageUrl = null,
                source = "The Guardian",
                author = null,
                category = Category.World,
                publishedAt = "28m ago",
                isBookmarked = false,
            ),
            onArticleClick = {},
            onBookmarkClick = {},
        )
    }
}

@Preview
@Composable
private fun ArticleHeroCardBookmarkedDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ArticleHeroCard(
            article = Article(
                id = "2",
                title = "Apple unveils on-device AI model that runs entirely offline",
                description = "",
                content = null,
                url = "",
                imageUrl = null,
                source = "The Verge",
                author = null,
                category = Category.Technology,
                publishedAt = "12m ago",
                isBookmarked = true,
            ),
            onArticleClick = {},
            onBookmarkClick = {},
        )
    }
}