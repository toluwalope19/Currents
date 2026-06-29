package com.app.currents.ui.screens.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.currents.domain.model.Article
import com.app.currents.presentation.explore.ExploreUiEffect
import com.app.currents.presentation.explore.ExploreUiEvent
import com.app.currents.presentation.explore.ExploreUiState
import com.app.currents.presentation.explore.ExploreViewModel
import com.app.currents.ui.components.ArticleHeroCard
import com.app.currents.ui.components.ArticleListItemSkeleton
import com.app.currents.ui.components.ExploreFilter
import com.app.currents.ui.components.ExploreGridCard
import com.app.currents.ui.components.FilterBottomSheet
import com.app.currents.ui.components.HeroCardSkeleton
import com.app.currents.ui.theme.CurrentsIcons
import com.app.currents.ui.theme.CurrentsTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

// Groups articles into rows for the mosaic pattern
data class MosaicRow(
    val type: RowType,
    val articles: List<Article>,
)

enum class RowType {
    TWO_EQUAL,          // Row 1: [equal] [equal]
    LARGE_LEFT,         // Row 2: [large] [small] [small]
    LARGE_RIGHT,        // Row 3: [small] [large]
}

fun buildMosaicRows(articles: List<Article>): List<MosaicRow> {
    val rows = mutableListOf<MosaicRow>()
    var index = 0
    var rowPattern = 0

    while (index < articles.size) {
        when (rowPattern % 3) {
            0 -> { // TWO_EQUAL — needs 2
                if (index + 1 < articles.size) {
                    rows.add(MosaicRow(RowType.TWO_EQUAL, articles.subList(index, index + 2)))
                    index += 2
                } else break
            }
            1 -> { // LARGE_LEFT — needs 3
                if (index + 2 < articles.size) {
                    rows.add(MosaicRow(RowType.LARGE_LEFT, articles.subList(index, index + 3)))
                    index += 3
                } else {
                    // fallback to TWO_EQUAL if not enough
                    rows.add(MosaicRow(RowType.TWO_EQUAL, articles.subList(index, minOf(index + 2, articles.size))))
                    break
                }
            }
            2 -> { // LARGE_RIGHT — needs 2
                if (index + 1 < articles.size) {
                    rows.add(MosaicRow(RowType.LARGE_RIGHT, articles.subList(index, index + 2)))
                    index += 2
                } else break
            }
        }
        rowPattern++
    }
    return rows
}

@Composable
fun ExploreScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: ExploreViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExploreUiEffect.NavigateToArticle -> onArticleClick(effect.article)
            }
        }
    }

    ExploreContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreContent(
    uiState: ExploreUiState,
    onEvent: (ExploreUiEvent) -> Unit,
) {
    val articles = uiState.articles
    val heroArticle = articles.firstOrNull()
    val gridArticles = if (articles.size > 1) articles.drop(1) else emptyList()
    val mosaicRows = remember(gridArticles) { buildMosaicRows(gridArticles) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf(ExploreFilter()) }

    val liveArticles = remember(articles) {
        articles.filter { !it.isBreaking }.take(8)
    }
    var liveArticleIndex by remember { mutableIntStateOf(0) }
    val slideOffset = remember { Animatable(0f) }

    LaunchedEffect(liveArticles) {
        while (true) {
            delay(3000)
            if (liveArticles.size > 1) {
                slideOffset.animateTo(-30f, animationSpec = tween(300))
                liveArticleIndex = (liveArticleIndex + 1) % liveArticles.size
                slideOffset.snapTo(30f)
                slideOffset.animateTo(0f, animationSpec = tween(300))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && articles.isNotEmpty(),
            onRefresh = {},
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 64.dp, // height of topbar + status bar
                    bottom = 16.dp,
                ),
            ) {
                // Hero card
                item {
                    if (uiState.isLoading && articles.isEmpty()) {
                        HeroCardSkeleton(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    } else {
                        heroArticle?.let { article ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ArticleHeroCard(
                                    article = article,
                                    onArticleClick = {
                                        onEvent(ExploreUiEvent.OnArticleClicked(article))
                                    },
                                    onBookmarkClick = {},
                                    showLiveBadge = article.isLive,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Mosaic grid
                if (uiState.isLoading && articles.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ArticleListItemSkeleton(modifier = Modifier.weight(1f))
                                ArticleListItemSkeleton(modifier = Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ArticleListItemSkeleton(modifier = Modifier.weight(1.2f))
                                Column(
                                    modifier = Modifier.weight(0.8f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    ArticleListItemSkeleton(modifier = Modifier.fillMaxWidth())
                                    ArticleListItemSkeleton(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                } else {
                    items(
                        items = mosaicRows,
                        key = { row -> row.articles.first().id },
                    ) { row ->
                        MosaicRowItem(
                            row = row,
                            isFirstRow = mosaicRows.indexOf(row) == 0,
                            liveArticle = if (mosaicRows.indexOf(row) == 0)
                                liveArticles.getOrNull(liveArticleIndex) else null,
                            slideOffset = slideOffset.value,
                            onArticleClick = { onEvent(ExploreUiEvent.OnArticleClicked(it)) },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Fixed top bar — overlays the scrolling content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .statusBarsPadding(),
        ) {
            ExploreTopBar(onFilterClick = {
                showFilterSheet = true
            })
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                filter = currentFilter,
                onFilterChanged = { currentFilter = it },
                onDismiss = { showFilterSheet = false },
                onApply = { showFilterSheet = false },
            )
        }
    }
}

@Composable
private fun MosaicRowItem(
    row: MosaicRow,
    isFirstRow: Boolean,
    liveArticle: Article?,
    slideOffset: Float,
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (row.type) {
        RowType.TWO_EQUAL -> {
            val a = row.articles.getOrNull(0) ?: return
            val b = row.articles.getOrNull(1) ?: return
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExploreGridCard(
                    article = a,
                    onClick = { onArticleClick(a) },
                    isLive = false,
                    modifier = Modifier.weight(1f).height(160.dp),
                )
                ExploreGridCard(
                    article = if (isFirstRow) liveArticle ?: b else b,
                    onClick = { onArticleClick(if (isFirstRow) liveArticle ?: b else b) },
                    isLive = isFirstRow,
                    slideOffset = if (isFirstRow) slideOffset else 0f,
                    modifier = Modifier.weight(1f).height(160.dp),
                )
            }
        }

        RowType.LARGE_LEFT -> {
            val a = row.articles.getOrNull(0) ?: return
            val b = row.articles.getOrNull(1)
            val c = row.articles.getOrNull(2)
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExploreGridCard(
                    article = a,
                    onClick = { onArticleClick(a) },
                    isLive = false,
                    modifier = Modifier.weight(1.2f).height(200.dp),
                )
                Column(
                    modifier = Modifier.weight(0.8f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    b?.let {
                        ExploreGridCard(
                            article = it,
                            onClick = { onArticleClick(it) },
                            isLive = false,
                            modifier = Modifier.fillMaxWidth().height(96.dp),
                        )
                    }
                    c?.let {
                        ExploreGridCard(
                            article = it,
                            onClick = { onArticleClick(it) },
                            isLive = false,
                            modifier = Modifier.fillMaxWidth().height(96.dp),
                        )
                    }
                }
            }
        }

        RowType.LARGE_RIGHT -> {
            val a = row.articles.getOrNull(0) ?: return
            val b = row.articles.getOrNull(1) ?: return
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ExploreGridCard(
                    article = a,
                    onClick = { onArticleClick(a) },
                    isLive = false,
                    modifier = Modifier.weight(0.8f).height(160.dp),
                )
                ExploreGridCard(
                    article = b,
                    onClick = { onArticleClick(b) },
                    isLive = false,
                    modifier = Modifier.weight(1.2f).height(160.dp),
                )
            }
        }
    }
}

@Composable
private fun ExploreTopBar(
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Explore",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
        )

        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
        ) {
            Icon(
                painter = painterResource(CurrentsIcons.Tune),
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ExploreScreenDarkPreview() {
    CurrentsTheme(darkTheme = true) {
        ExploreContent(
            uiState = ExploreUiState(isLoading = false),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun ExploreScreenLightPreview() {
    CurrentsTheme(darkTheme = false) {
        ExploreContent(
            uiState = ExploreUiState(isLoading = false),
            onEvent = {},
        )
    }
}