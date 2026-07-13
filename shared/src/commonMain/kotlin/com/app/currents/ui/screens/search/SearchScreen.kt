package com.app.currents.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.search.*
import com.app.currents.ui.components.ArticleListItem
import com.app.currents.ui.components.ScatteredBubbleLayout
import com.app.currents.ui.theme.Accent
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import com.app.currents.ui.theme.toColor
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchUiEffect.NavigateToArticle -> onArticleClick(effect.article)
                SearchUiEffect.StartVoiceInput -> { /* TODO: expect/actual STT */ }
            }
        }
    }

    SearchContent(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
internal fun SearchContent(
    uiState: SearchUiState,
    onEvent: (SearchUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        SearchBar(
            query = uiState.query,
            onQueryChange = { onEvent(SearchUiEvent.OnQueryChange(it)) },
            onClear = { onEvent(SearchUiEvent.OnClearQuery) },
            onVoice = { onEvent(SearchUiEvent.OnVoiceClick) },
        )
        Spacer(Modifier.height(12.dp))
        ScopeTabs(
            selected = uiState.scope,
            onSelect = { onEvent(SearchUiEvent.OnScopeChange(it)) },
        )
        Spacer(Modifier.height(8.dp))

        if (uiState.showResults) {
            ResultsArea(uiState = uiState, onEvent = onEvent)
        } else {
            DiscoveryArea(uiState = uiState, onEvent = onEvent)
        }
    }
}

// ── Search bar ────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onVoice: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(
            painter = painterResource(CurrentsIcons.Search),
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search news, topics, sources",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = Brush.verticalGradient(listOf(Accent, Accent)),
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                painter = painterResource(CurrentsIcons.Close),
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onClear() },
            )
            Spacer(Modifier.width(10.dp))
        }
        Icon(
            painter = painterResource(CurrentsIcons.Mic),
            contentDescription = "Voice search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(20.dp)
                .clickable { onVoice() },
        )
    }
}

// ── Scope tabs ────────────────────────────────────────────────────────────────

@Composable
private fun ScopeTabs(selected: SearchScope, onSelect: (SearchScope) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SearchScope.entries.forEach { scope ->
            val isSelected = scope == selected
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
                    .clickable { onSelect(scope) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = scope.label,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

// ── Discovery (resting state) ─────────────────────────────────────────────────

@Composable
private fun DiscoveryArea(uiState: SearchUiState, onEvent: (SearchUiEvent) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Trending header
        item {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Trending now",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        // Trending 2-col grid
        item {
            val rows = uiState.trending.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { topic ->
                            Box(Modifier.weight(1f)) {
                                TrendingCard(topic) { onEvent(SearchUiEvent.OnTrendingClick(topic)) }
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        // Browse topics header
        item {
            Column {
                Text(
                    text = "Browse topics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${uiState.selectedCount} selected · tap to personalise",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Bubble cloud — reuse ScatteredBubbleLayout from Onboarding
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),  // fixed height so LazyColumn can measure it
            ) {
                ScatteredBubbleLayout(
                    categories = uiState.topics.map { it.category },
                    selectedCategories = uiState.topics
                        .filter { it.isSelected }
                        .map { it.category },
                    onCategoryToggled = { onEvent(SearchUiEvent.OnTopicToggle(it)) },
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Trending card ─────────────────────────────────────────────────────────────

@Composable
private fun TrendingCard(topic: TrendingTopic, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp),
            )
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(topic.category.toColor()),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = topic.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = topic.source,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ── Results area ──────────────────────────────────────────────────────────────

@Composable
private fun ResultsArea(uiState: SearchUiState, onEvent: (SearchUiEvent) -> Unit) {
    when {
        uiState.isSearching -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Accent)
        }

        uiState.error != null -> CenteredMessage(
            title = "Couldn't search",
            subtitle = uiState.error,
        )

        uiState.hasSearched && uiState.results.isEmpty() -> CenteredMessage(
            title = "No results",
            subtitle = "Nothing found for \"${uiState.query}\". Try a different term.",
        )

        else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.results, key = { it.id }) { article ->
                ArticleListItem(
                    article = article,
                    onClick = { onEvent(SearchUiEvent.OnResultClick(article)) },
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun CenteredMessage(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun SearchScreenDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        SearchContent(
            uiState = SearchUiState(
                trending = listOf(
                    TrendingTopic("Gaza Ceasefire", "BBC News", Category.Politics),
                    TrendingTopic("Apple WWDC 2026", "TechCrunch", Category.Technology),
                    TrendingTopic("Champions League", "ESPN", Category.Sports),
                    TrendingTopic("Climate Summit", "Reuters", Category.Science),
                    TrendingTopic("OpenAI GPT-5", "The Verge", Category.Business),
                    TrendingTopic("Nigeria Elections", "Channels TV", Category.Politics),
                ),
                topics = Category.onboardingTopics.mapIndexed { i, cat ->
                    TopicSelectable(cat, isSelected = i < 2)
                },
            ),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SearchScreenLightPreview() {
    CurrentsTheme(darkTheme = false) {
        SearchContent(
            uiState = SearchUiState(
                trending = listOf(
                    TrendingTopic("Gaza Ceasefire", "BBC News", Category.Politics),
                    TrendingTopic("Apple WWDC 2026", "TechCrunch", Category.Technology),
                    TrendingTopic("Champions League", "ESPN", Category.Sports),
                    TrendingTopic("Climate Summit", "Reuters", Category.Science),
                    TrendingTopic("OpenAI GPT-5", "The Verge", Category.Business),
                    TrendingTopic("Nigeria Elections", "Channels TV", Category.Politics),
                ),
                topics = Category.onboardingTopics.mapIndexed { i, cat ->
                    TopicSelectable(cat, isSelected = i < 2)
                },
            ),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SearchScreenResultsPreview() {
    CurrentsTheme(darkTheme = true) {
        SearchContent(
            uiState = SearchUiState(
                query = "AI",
                hasSearched = true,
                results = listOf(
                    Article(
                        id = "1",
                        title = "OpenAI releases GPT-5 with major reasoning improvements",
                        description = "The new model outperforms its predecessor on every benchmark.",
                        source = "The Verge",
                        imageUrl = null,
                        url = "",
                        publishedAt = "2026-06-29",
                        category = Category.Technology,
                        isBreaking = false,
                        content = null,
                        author = "",
                        isBookmarked = false,
                        isLive = false,
                    ),
                    Article(
                        id = "2",
                        title = "Google DeepMind announces Gemini Ultra 2",
                        description = "DeepMind's latest model targets scientific research.",
                        source = "Wired",
                        imageUrl = null,
                        url = "",
                        publishedAt = "2026-06-29",
                        category = Category.Technology,
                        isBreaking = false,
                        content = null,
                        author = "",
                        isBookmarked = false,
                        isLive = false,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SearchScreenEmptyPreview() {
    CurrentsTheme(darkTheme = true) {
        SearchContent(
            uiState = SearchUiState(
                query = "xyznotfound",
                hasSearched = true,
                results = emptyList(),
            ),
            onEvent = {},
        )
    }
}