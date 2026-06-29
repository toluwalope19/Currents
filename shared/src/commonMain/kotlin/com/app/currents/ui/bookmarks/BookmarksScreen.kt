package com.app.currents.ui.bookmarks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.currents.domain.model.Article
import com.app.currents.presentation.bookmarks.*
import com.app.currents.ui.components.ArticleListItem
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarksScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: BookmarksViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookmarksUiEffect.NavigateToArticle -> onArticleClick(effect.article)
            }
        }
    }

    BookmarksContent(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
private fun BookmarksContent(
    uiState: BookmarksUiState,
    onEvent: (BookmarksUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(12.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Saved",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                painter = painterResource(CurrentsIcons.Sort),
                contentDescription = "Sort",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onEvent(BookmarksUiEvent.OnToggleSort) },
            )
        }

        Spacer(Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatItem(modifier = Modifier.weight(1f), value = uiState.savedCount.toString(), label = "Saved")
            StatItem(modifier = Modifier.weight(1f), value = uiState.offlineCount.toString(), label = "Offline")
            StatItem(modifier = Modifier.weight(1f), value = uiState.unreadCount.toString(), label = "Unread")
        }

        Spacer(Modifier.height(16.dp))

        // Filter tabs
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BookmarkFilter.entries.forEach { filter ->
                val isSelected = filter == uiState.selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) Modifier.background(Accent)
                            else Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(20.dp),
                            )
                        )
                        .clickable { onEvent(BookmarksUiEvent.OnFilterSelected(filter)) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = filter.label,
                        color = if (isSelected) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Accent)
            }
            uiState.filteredBookmarks.isEmpty() -> EmptyBookmarks()
            else -> BookmarksList(
                grouped = uiState.grouped,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun BookmarksList(
    grouped: Map<String, List<Article>>,
    onEvent: (BookmarksUiEvent) -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        grouped.forEach { (section, articles) ->
            item(key = section) {
                SectionHeader(title = section)
            }
            items(articles, key = { it.id }) { article ->
                SwipeableBookmarkCard(
                    article = article,
                    onClick = { onEvent(BookmarksUiEvent.OnArticleClick(article)) },
                    onDelete = { onEvent(BookmarksUiEvent.OnRemoveBookmark(article.id)) },
                )
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableBookmarkCard(
    article: Article,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    Color(0xFFE53935) else Color.Transparent,
                label = "swipe_bg",
            )
            val scale by animateFloatAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    1f else 0.75f,
                label = "swipe_scale",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(scale),
                ) {
                    Icon(
                        painter = painterResource(CurrentsIcons.Delete),
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
    ) {
        ArticleListItem(
            article = article,
            onClick = onClick,
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Composable
private fun EmptyBookmarks() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🔖", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No saved articles",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Bookmark articles to read them later, even offline.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun BookmarksScreenDarkPreview() {
    com.app.currents.ui.theme.CurrentsTheme(darkTheme = true) {
        BookmarksContent(
            uiState = BookmarksUiState(
                bookmarks = previewArticles,
                filteredBookmarks = previewArticles,
            ),
            onEvent = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun BookmarksScreenLightPreview() {
    com.app.currents.ui.theme.CurrentsTheme(darkTheme = false) {
        BookmarksContent(
            uiState = BookmarksUiState(
                bookmarks = previewArticles,
                filteredBookmarks = previewArticles,
            ),
            onEvent = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun BookmarksScreenEmptyPreview() {
    com.app.currents.ui.theme.CurrentsTheme(darkTheme = true) {
        BookmarksContent(
            uiState = BookmarksUiState(
                bookmarks = emptyList(),
                filteredBookmarks = emptyList(),
            ),
            onEvent = {},
        )
    }
}

private val previewArticles = listOf(
    Article(
        id = "1",
        title = "Global leaders reach historic accord at climate summit",
        description = "",
        content = null,
        url = "",
        imageUrl = null,
        source = "The Guardian",
        author = null,
        category = com.app.currents.domain.model.Category.World,
        publishedAt = "2h ago",
        isBookmarked = true,
    ),
    Article(
        id = "2",
        title = "Apple unveils on-device AI model that runs offline",
        description = "",
        content = null,
        url = "",
        imageUrl = null,
        source = "The Verge",
        author = null,
        category = com.app.currents.domain.model.Category.Technology,
        publishedAt = "10h ago",
        isBookmarked = true,
    ),
    Article(
        id = "3",
        title = "Markets rally as inflation cools to three-year low",
        description = "",
        content = null,
        url = "",
        imageUrl = null,
        source = "Reuters",
        author = null,
        category = com.app.currents.domain.model.Category.Business,
        publishedAt = "3d ago",
        isBookmarked = true,
    ),
)