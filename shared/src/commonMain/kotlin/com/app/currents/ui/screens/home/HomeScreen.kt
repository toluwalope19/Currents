package com.app.currents.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.home.HomeUiEffect
import com.app.currents.presentation.home.HomeUiEvent
import com.app.currents.presentation.home.HomeViewModel
import com.app.currents.ui.components.ArticleHeroCard
import com.app.currents.ui.components.ArticleListItem
import com.app.currents.ui.components.ArticleListItemSkeleton
import com.app.currents.ui.components.BreakingNewsBanner
import com.app.currents.ui.components.HeroCardSkeleton
import com.app.currents.ui.components.OfflineBanner
import com.app.currents.ui.components.StoryItem
import com.app.currents.ui.components.StoryItemData
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onArticleClick: (Article) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToArticle -> onArticleClick(effect.article)
            }
        }
    }

    HomeContent(
        uiState = uiState,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: com.app.currents.presentation.home.HomeUiState,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
) {
    val articles = uiState.articles
    val heroArticle = articles.firstOrNull()
    val listArticles = if (articles.size > 1) articles.drop(1) else emptyList()
    val breakingArticle = articles.firstOrNull { it.isBreaking }

    // Story items from user's selected categories
    val storyItems = buildList {
        add(
            StoryItemData(
                id = "foryou",
                label = "For You",
                gradientColors = emptyList(),
                isForYou = true,
            )
        )
        Category.onboardingTopics.take(8).forEach { category ->
            add(
                StoryItemData(
                    id = category.apiValue,
                    label = category.label,
                    gradientColors = listOf(
                        category.toColor().copy(alpha = 0.8f),
                        category.toColor().copy(alpha = 0.2f),
                    ),
                )
            )
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onEvent(HomeUiEvent.OnRefresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // Top bar
            item {
                HomeTopBar(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onBellClick = {},
                )
            }

            item {
                OfflineBanner(isVisible = uiState.isOffline)
            }

            // Stories row
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(storyItems) { story ->
                        StoryItem(
                            data = story,
                            onClick = {
                                if (story.isForYou) {
                                    onEvent(HomeUiEvent.OnCategorySelected(Category.Top))
                                } else {
                                    val category = Category.fromApiValue(story.id)
                                    onEvent(HomeUiEvent.OnCategorySelected(category))
                                }
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Breaking news banner
            item {
                BreakingNewsBanner(
                    headline = breakingArticle?.title ?: "",
                    visible = breakingArticle != null,
                    onClick = {
                        breakingArticle?.let {
                            onEvent(HomeUiEvent.OnArticleClicked(it))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                if (breakingArticle != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Hero card
            heroArticle?.let { article ->
                item {
                    ArticleHeroCard(
                        article = article,
                        onArticleClick = { onEvent(HomeUiEvent.OnArticleClicked(article)) },
                        onBookmarkClick = {},
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Top Stories header
            if (listArticles.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = "Top Stories",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                        TextButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(
                                text = "See all →",
                                color = Accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Article list
                itemsIndexed(listArticles) { index, article ->
                    ArticleListItem(
                        article = article,
                        onClick = { onEvent(HomeUiEvent.OnArticleClicked(article)) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    if (index < listArticles.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Loading state
            // Loading state — shimmer skeletons
            if (uiState.isLoading && articles.isEmpty()) {
                item {
                    HeroCardSkeleton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = "Top Stories",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(4) {
                    ArticleListItemSkeleton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Error state
            uiState.error?.let { error ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        HomeContent(
            uiState = com.app.currents.presentation.home.HomeUiState(
                isLoading = false,
            ),
            isDarkTheme = true,
            onThemeToggle = {},
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun HomeScreenLightPreview() {
    CurrentsTheme(darkTheme = false) {
        HomeContent(
            uiState = com.app.currents.presentation.home.HomeUiState(
                isLoading = false,
            ),
            isDarkTheme = false,
            onThemeToggle = {},
            onEvent = {},
        )
    }
}