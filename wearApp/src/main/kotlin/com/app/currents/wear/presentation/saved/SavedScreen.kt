package com.app.currents.wear.presentation.saved

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.*
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.toColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedScreen(
    onArticleClick: (String) -> Unit,
    viewModel: SavedViewModel = koinViewModel(),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("🔖", fontSize = 14.sp)
                        Text(
                            text = "Saved",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                    Text(
                        text = "${uiState.bookmarks.size}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                }
            }

            if (uiState.isLoading) {
                items(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E1E)),
                    )
                }
            } else if (uiState.bookmarks.isEmpty()) {
                item {
                    Text(
                        text = "No saved articles",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                    )
                }
            } else {
                val pairs = uiState.bookmarks.chunked(2)
                items(pairs.size) { index ->
                    val pair = pairs[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        pair.forEach { article ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                article.category.toColor().copy(alpha = 0.7f),
                                                article.category.toColor().copy(alpha = 0.2f),
                                            )
                                        )
                                    )
                                    .clickable { onArticleClick(article.id) },
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = article.category.label.take(4).uppercase(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}