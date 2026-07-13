package com.app.currents.presentation.search

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetFeedUseCase
import com.app.currents.domain.usecase.SearchArticlesUseCase
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.runViewModelTest
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchViewModelTest {

    private fun buildViewModel(
        remote: FakeArticleRemoteRepository = FakeArticleRemoteRepository(),
        local: FakeArticleLocalRepository = FakeArticleLocalRepository(),
    ) = SearchViewModel(
        searchArticles = SearchArticlesUseCase(remote),
        getFeed = GetFeedUseCase(local, remote),
    )

    @Test
    fun loads_default_topics_and_trending_on_init() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(latestResult = Result.success(listOf(testArticle(id = "1"))))
        val viewModel = buildViewModel(remote = remote)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.topics.isNotEmpty())
        assertTrue(viewModel.uiState.value.topics.first { it.category == Category.Technology }.isSelected)
        assertEquals(1, viewModel.uiState.value.trending.size)
    }

    @Test
    fun query_change_debounces_then_returns_search_results() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { query -> Result.success(listOf(testArticle(id = query))) },
        )
        val viewModel = buildViewModel(remote = remote)
        advanceUntilIdle()

        viewModel.onEvent(SearchUiEvent.OnQueryChange("kotlin"))
        advanceUntilIdle()

        assertEquals(listOf(testArticle(id = "kotlin")), viewModel.uiState.value.results)
        assertTrue(viewModel.uiState.value.hasSearched)
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun clear_query_resets_search_state() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { query -> Result.success(listOf(testArticle(id = query))) },
        )
        val viewModel = buildViewModel(remote = remote)
        advanceUntilIdle()
        viewModel.onEvent(SearchUiEvent.OnQueryChange("kotlin"))
        advanceUntilIdle()

        viewModel.onEvent(SearchUiEvent.OnClearQuery)
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.query)
        assertEquals(emptyList(), viewModel.uiState.value.results)
        assertFalse(viewModel.uiState.value.hasSearched)
    }

    @Test
    fun search_failure_surfaces_error_message() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { Result.failure(RuntimeException("offline")) },
        )
        val viewModel = buildViewModel(remote = remote)
        advanceUntilIdle()

        viewModel.onEvent(SearchUiEvent.OnQueryChange("kotlin"))
        advanceUntilIdle()

        assertEquals("offline", viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.hasSearched)
    }

    @Test
    fun toggling_a_topic_flips_its_selection() = runViewModelTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        val wasSelected = viewModel.uiState.value.topics.first { it.category == Category.Technology }.isSelected

        viewModel.onEvent(SearchUiEvent.OnTopicToggle(Category.Technology))
        advanceUntilIdle()

        val isSelectedNow = viewModel.uiState.value.topics.first { it.category == Category.Technology }.isSelected
        assertEquals(!wasSelected, isSelectedNow)
    }

    @Test
    fun trending_click_sets_query_to_topic_title() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { query -> Result.success(listOf(testArticle(id = query))) },
        )
        val viewModel = buildViewModel(remote = remote)
        advanceUntilIdle()
        val topic = TrendingTopic(title = "Breaking News", source = "Reuters", category = Category.World)

        viewModel.onEvent(SearchUiEvent.OnTrendingClick(topic))
        advanceUntilIdle()

        assertEquals("Breaking News", viewModel.uiState.value.query)
    }
}
