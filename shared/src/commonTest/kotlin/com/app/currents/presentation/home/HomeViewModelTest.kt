package com.app.currents.presentation.home

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.domain.usecase.GetFeedUseCase
import com.app.currents.domain.usecase.HasCachedArticlesUseCase
import com.app.currents.domain.usecase.RefreshFeedUseCase
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.FakeNetworkMonitor
import com.app.currents.fakes.runViewModelTest
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeViewModelTest {

    private fun buildViewModel(
        local: FakeArticleLocalRepository = FakeArticleLocalRepository(),
        remote: FakeArticleRemoteRepository = FakeArticleRemoteRepository(),
        networkMonitor: FakeNetworkMonitor = FakeNetworkMonitor(),
    ): HomeViewModel = HomeViewModel(
        getFeedUseCase = GetFeedUseCase(local, remote),
        getByCategoryUseCase = GetByCategoryUseCase(local, remote),
        refreshFeedUseCase = RefreshFeedUseCase(local, remote),
        hasCachedArticlesUseCase = HasCachedArticlesUseCase(local),
        networkMonitor = networkMonitor,
    )

    @Test
    fun loads_top_feed_on_init() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(latestResult = Result.success(listOf(testArticle(id = "1"))))
        val viewModel = buildViewModel(remote = remote)

        advanceUntilIdle()

        assertEquals(listOf(testArticle(id = "1")), viewModel.uiState.value.articles)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun switching_category_loads_that_categorys_feed() = runViewModelTest {
        val remote = FakeArticleRemoteRepository(
            byCategoryResult = { category -> Result.success(listOf(testArticle(id = "cat", category = category))) },
        )
        val viewModel = buildViewModel(remote = remote)
        advanceUntilIdle()

        viewModel.onEvent(HomeUiEvent.OnCategorySelected(Category.Technology))
        advanceUntilIdle()

        assertEquals(Category.Technology, viewModel.uiState.value.selectedCategory)
        assertEquals(listOf(testArticle(id = "cat", category = Category.Technology)), viewModel.uiState.value.articles)
    }

    @Test
    fun refresh_toggles_isRefreshing_and_updates_via_refresh_use_case() = runViewModelTest {
        // HomeViewModel.refresh() always passes the non-null selectedCategory (Category.Top by
        // default) to RefreshFeedUseCase, so it takes the getByCategory branch, not getLatest.
        val local = FakeArticleLocalRepository()
        val remote = FakeArticleRemoteRepository(latestResult = Result.success(emptyList()))
        val viewModel = buildViewModel(local = local, remote = remote)
        advanceUntilIdle()

        remote.byCategoryResult = { Result.success(listOf(testArticle(id = "refreshed", category = Category.Top))) }
        viewModel.onEvent(HomeUiEvent.OnRefresh)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(listOf(testArticle(id = "refreshed", category = Category.Top)), local.articlesState.value)
    }

    @Test
    fun reflects_network_monitor_offline_state() = runViewModelTest {
        val networkMonitor = FakeNetworkMonitor(online = true)
        val viewModel = buildViewModel(networkMonitor = networkMonitor)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isOffline)

        networkMonitor.setOnline(false)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOffline)
    }
}
