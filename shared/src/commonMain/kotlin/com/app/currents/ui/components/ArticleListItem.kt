package com.app.currents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import com.app.currents.util.formatRelativeTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun ArticleListItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Left — category chip + headline + source
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            CategoryChip(
                category = article.category,
                isSelected = false,
            )

            Text(
                text = article.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${article.source} · ${formatRelativeTime(article.publishedAt)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        // Right — thumbnail
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {

            SubcomposeAsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp),
                error = {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        article.category.toColor().copy(alpha = 0.6f),
                                        article.category.toColor().copy(alpha = 0.2f),
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(CurrentsIcons.Globe),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )

        }
    }
}

@Preview
@Composable
private fun ArticleListItemDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ArticleListItem(
            article = Article(
                id = "1",
                title = "Apple unveils on-device AI model that runs entirely offline",
                description = "",
                content = null,
                url = "",
                imageUrl = null,
                source = "The Verge",
                author = null,
                category = Category.Technology,
                publishedAt = "12m ago",
                isBookmarked = false,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun ArticleListItemLightPreview() {
    CurrentsTheme(darkTheme = false) {
        ArticleListItem(
            article = Article(
                id = "2",
                title = "Markets rally as inflation cools to lowest level in three years",
                description = "",
                content = null,
                url = "",
                imageUrl = null,
                source = "Reuters",
                author = null,
                category = Category.Business,
                publishedAt = "34m ago",
                isBookmarked = false,
            ),
            onClick = {},
        )
    }
}