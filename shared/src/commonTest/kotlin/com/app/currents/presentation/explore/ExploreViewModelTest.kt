package com.app.currents.presentation.explore

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.FakeNetworkMonitor
import com.app.currents.fakes.runViewModelTest
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExploreViewModelTest {

    private fun buildViewModel(
        local: FakeArticleLocalRepository = FakeArticleLocalRepository(),
        remote: FakeArticleRemoteRepository = FakeArticleRemoteRepository(
            byCategoryResult = { category -> Result.success(listOf(testArticle(id = category.apiValue, category = category))) },
        ),
        networkMonitor: FakeNetworkMonitor = FakeNetworkMonitor(),
    ) = ExploreViewModel(
        getByCategoryUseCase = GetByCategoryUseCase(local, remote),
        networkMonitor = networkMonitor,
    )

    @Test
    fun loads_a_mosaic_of_articles_across_categories_on_init() = runViewModelTest {
        val viewModel = buildViewModel()

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.articles.isNotEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun clicking_a_category_loads_only_that_categorys_articles() = runViewModelTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onEvent(ExploreUiEvent.OnCategoryClicked(Category.Business))
        advanceUntilIdle()

        assertEquals(Category.Business, viewModel.uiState.value.selectedCategory)
        assertTrue(viewModel.uiState.value.articles.all { it.category == Category.Business })
    }

    @Test
    fun back_from_category_clears_selection_and_articles() = runViewModelTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.onEvent(ExploreUiEvent.OnCategoryClicked(Category.Business))
        advanceUntilIdle()

        viewModel.onEvent(ExploreUiEvent.OnBackFromCategory)

        assertNull(viewModel.uiState.value.selectedCategory)
        assertEquals(emptyList(), viewModel.uiState.value.articles)
    }

    @Test
    fun reflects_network_monitor_offline_state() = runViewModelTest {
        val networkMonitor = FakeNetworkMonitor(online = true)
        val viewModel = buildViewModel(networkMonitor = networkMonitor)
        advanceUntilIdle()

        networkMonitor.setOnline(false)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOffline)
    }
}
