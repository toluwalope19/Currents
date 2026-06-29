package com.app.currents.ui.screens.article

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.article.ArticleUiEffect
import com.app.currents.presentation.article.ArticleUiEvent
import com.app.currents.presentation.article.ArticleUiState
import com.app.currents.presentation.article.ArticleViewModel
import com.app.currents.ui.components.AISummaryCard
import com.app.currents.ui.components.CategoryChip
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.LocalDarkTheme
import com.app.currents.ui.theme.toColor
import com.app.currents.util.formatRelativeTime
import com.app.currents.util.openUrl
import com.app.currents.util.shareText
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArticleDetailScreen(
    articleId: String? = null,
    article: Article? = null,
    onBack: () -> Unit,
    viewModel: ArticleViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(articleId, article) {
        when {
            article != null -> viewModel.onEvent(ArticleUiEvent.OnArticleLoaded(article))
            articleId != null -> viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById(articleId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ArticleUiEffect.NavigateBack -> onBack()
                is ArticleUiEffect.OpenUrl -> openUrl(effect.url)
                is ArticleUiEffect.ShareArticle -> shareText(effect.title, effect.url)
            }
        }
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Accent)
            }
        }
        uiState.article != null -> {
            ArticleDetailContent(
                uiState = uiState,
                onBack = onBack,
                onBookmarkToggle = {
                    viewModel.onEvent(ArticleUiEvent.OnBookmarkToggled)
                },
                onShare = {
                    viewModel.onEvent(ArticleUiEvent.OnShareClicked)
                },
                onOpenBrowser = {
                    viewModel.onEvent(ArticleUiEvent.OnReadFullArticleClicked)
                },
            )
        }
    }
}

@Composable
private fun ArticleDetailContent(
    uiState: ArticleUiState,
    onBack: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onShare: () -> Unit,
    onOpenBrowser: () -> Unit,
) {
    val article = uiState.article ?: return
    val isDark = LocalDarkTheme.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
            ) {
                if (article.imageUrl != null) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        article.category.toColor().copy(alpha = 0.8f),
                                        Color(0xFF0A0A0A),
                                    )
                                )
                            )
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.surface,
                                ),
                                startY = 120f,
                            )
                        )
                )

                CategoryChip(
                    category = article.category,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Source row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = article.source.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = article.source,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "· ${formatRelativeTime(article.publishedAt)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .border(
                                width = 1.dp,
                                color = Accent,
                                shape = RoundedCornerShape(100.dp),
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {},
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Follow",
                            color = Accent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Headline
                Text(
                    text = article.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 30.sp,
                )

                // Author
                article.author?.let { author ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "By $author",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // AI Summary — purple glow in dark mode
                Box(
                    modifier = if (isDark) {
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Accent.copy(alpha = 0.12f),
                                        Color.Transparent,
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                    } else {
                        Modifier.fillMaxWidth()
                    }
                ) {
                    AISummaryCard(
                        summary = uiState.aiSummary ?: article.description,
                        isLoading = uiState.isSummaryLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Body text
                val bodyText = when {
                    !article.content.isNullOrBlank() &&
                            !article.content.contains("ONLY AVAILABLE", ignoreCase = true) -> article.content
                    uiState.aiSummary != null && uiState.aiSummary != article.description -> article.description
                    else -> null
                }

                bodyText?.let { text ->
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Top floating buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(CurrentsIcons.Back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onShare,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Share),
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.isBookmarked) Accent
                            else Color.Black.copy(alpha = 0.4f)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBookmarkToggle,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            if (uiState.isBookmarked) CurrentsIcons.BookmarkFill
                            else CurrentsIcons.Bookmark
                        ),
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onShare,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(CurrentsIcons.Share),
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isBookmarked) Accent
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBookmarkToggle,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(
                                if (uiState.isBookmarked) CurrentsIcons.BookmarkFill
                                else CurrentsIcons.Bookmark
                            ),
                            contentDescription = "Bookmark",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Button(
                    onClick = onOpenBrowser,
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    modifier = Modifier.height(44.dp),
                ) {
                    Text(
                        text = "Open in browser",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(CurrentsIcons.External),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArticleDetailDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ArticleDetailContent(
            uiState = ArticleUiState(
                article = Article(
                    id = "1",
                    title = "Global leaders reach historic accord on emissions at climate summit",
                    description = "Delegates erupted in applause early Saturday as the summit's chair gavelled through an agreement that had eluded negotiators for nearly two weeks of round-the-clock talks.",
                    content = "The accord commits signatories to a tripling of installed renewable capacity within the decade.",
                    url = "",
                    imageUrl = null,
                    source = "BBC News",
                    author = "Sarah Mitchell",
                    category = Category.World,
                    publishedAt = "2h ago",
                    isBookmarked = false,
                ),
                isBookmarked = false,
                aiSummary = "Nearly 200 nations agreed to triple renewable capacity by 2035.",
                isSummaryLoading = false,
            ),
            onBack = {},
            onBookmarkToggle = {},
            onShare = {},
            onOpenBrowser = {},
        )
    }
}

@Preview
@Composable
private fun ArticleDetailLightPreview() {
    CurrentsTheme(darkTheme = false) {
        ArticleDetailContent(
            uiState = ArticleUiState(
                article = Article(
                    id = "1",
                    title = "Global leaders reach historic accord on emissions at climate summit",
                    description = "Delegates erupted in applause early Saturday as the summit's chair gavelled through an agreement that had eluded negotiators for nearly two weeks of round-the-clock talks.",
                    content = null,
                    url = "",
                    imageUrl = null,
                    source = "BBC News",
                    author = "Sarah Mitchell",
                    category = Category.World,
                    publishedAt = "2h ago",
                    isBookmarked = true,
                ),
                isBookmarked = true,
                aiSummary = null,
                isSummaryLoading = false,
            ),
            onBack = {},
            onBookmarkToggle = {},
            onShare = {},
            onOpenBrowser = {},
        )
    }
}