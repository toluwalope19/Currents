package com.app.currents.wear.presentation.article

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.*
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.toColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArticleScreen(
    articleId: String,
    onBack: () -> Unit,
    viewModel: WearArticleViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(articleId) {
        viewModel.loadArticle(articleId)
    }

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
            val article = uiState.article

            if (uiState.isLoading || article == null) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            indicatorColor = Accent,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(article.category.toColor().copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = article.category.label.uppercase(),
                            color = article.category.toColor(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                item {
                    Text(
                        text = article.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp,
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1E1E))
                            .padding(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(60.dp)
                                .background(Accent),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "✦ AI SUMMARY",
                                color = Accent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = article.description,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                article.content?.let { content ->
                    item {
                        Text(
                            text = content,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CompactChip(
                            onClick = {
                                if (uiState.isSpeaking) viewModel.stopReading()
                                else viewModel.readAloud()
                            },
                            label = {
                                Text(
                                    text = if (uiState.isSpeaking) "Stop" else "🔊 Read",
                                    fontSize = 11.sp,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ChipDefaults.chipColors(
                                backgroundColor = if (uiState.isSpeaking)
                                    Color(0xFFE53935).copy(alpha = 0.3f)
                                else Accent.copy(alpha = 0.3f),
                            ),
                        )
                        CompactChip(
                            onClick = { viewModel.toggleBookmark() },
                            label = {
                                Text(
                                    text = if (uiState.isBookmarked) "🔖 Saved" else "🔖 Save",
                                    fontSize = 11.sp,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ChipDefaults.chipColors(
                                backgroundColor = if (uiState.isBookmarked)
                                    Accent.copy(alpha = 0.5f)
                                else Color(0xFF1E1E1E),
                            ),
                        )
                    }
                }
            }
        }
    }
}