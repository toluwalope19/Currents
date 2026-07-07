package com.app.currents.wear.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.*
import com.app.currents.domain.model.Article
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.toColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedScreen(
    onArticleClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scalingLazyListState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = scalingLazyListState)
        },
    ) {
        ScalingLazyColumn(
            state = scalingLazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 32.dp, bottom = 16.dp,
                start = 8.dp, end = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "Top Stories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            if (uiState.isLoading) {
                items(3) { FeedCardSkeleton() }
            } else {
                items(uiState.articles.size) { index ->
                    val article = uiState.articles[index]
                    FeedCard(
                        article = article,
                        onClick = { onArticleClick(article.id) },
                    )
                }
                item {
                    Chip(
                        onClick = onSavedClick,
                        label = {
                            Text(text = "Saved articles", fontSize = 13.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ChipDefaults.chipColors(
                            backgroundColor = Accent.copy(alpha = 0.3f),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedCard(article: Article, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            article.category.toColor().copy(alpha = 0.8f),
                            article.category.toColor().copy(alpha = 0.3f),
                        )
                    )
                ),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(article.category.toColor().copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = article.category.label.take(4).uppercase(),
                    color = article.category.toColor(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = article.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun FeedCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E)),
    )
}