package com.app.currents.presentation.bookmarks

import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.fakes.FakeBookmarkRepository
import com.app.currents.fakes.runViewModelTest
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals

class BookmarksViewModelTest {

    private fun buildViewModel(repository: FakeBookmarkRepository) = BookmarksViewModel(
        getBookmarks = GetBookmarksUseCase(repository),
        removeBookmark = RemoveBookmarkUseCase(repository),
    )

    @Test
    fun loads_bookmarks_on_init() = runViewModelTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val viewModel = buildViewModel(repository)

        advanceUntilIdle()

        assertEquals(listOf(article), viewModel.uiState.value.bookmarks)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun removing_bookmark_updates_the_underlying_repository() = runViewModelTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val viewModel = buildViewModel(repository)
        advanceUntilIdle()

        viewModel.onEvent(BookmarksUiEvent.OnRemoveBookmark(article.id))
        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.uiState.value.bookmarks)
        assertEquals(emptyList(), repository.bookmarksState.value)
    }

    @Test
    fun article_click_sends_navigate_effect() = runViewModelTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val viewModel = buildViewModel(repository)
        advanceUntilIdle()

        viewModel.onEvent(BookmarksUiEvent.OnArticleClick(article))
        advanceUntilIdle()

        assertEquals(BookmarksUiEffect.NavigateToArticle(article), viewModel.effect.first())
    }

    @Test
    fun toggling_sort_flips_sortNewestFirst() = runViewModelTest {
        val viewModel = buildViewModel(FakeBookmarkRepository())
        advanceUntilIdle()
        val initial = viewModel.uiState.value.sortNewestFirst

        viewModel.onEvent(BookmarksUiEvent.OnToggleSort)
        advanceUntilIdle()

        assertEquals(!initial, viewModel.uiState.value.sortNewestFirst)
    }
}
