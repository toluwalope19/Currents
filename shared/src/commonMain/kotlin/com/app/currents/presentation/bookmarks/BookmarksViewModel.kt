package com.app.currents.presentation.bookmarks

import androidx.lifecycle.viewModelScope
import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val getBookmarks: GetBookmarksUseCase,
    private val removeBookmark: RemoveBookmarkUseCase,
) : BaseViewModel<BookmarksUiState, BookmarksUiEvent, BookmarksUiEffect>(
    initialState = BookmarksUiState(),
) {
    init {
        observeBookmarks()
    }

    override fun onEvent(event: BookmarksUiEvent) {
        when (event) {
            is BookmarksUiEvent.OnFilterSelected -> {
                setState { copy(selectedFilter = event.filter) }
                applyFilter()
            }
            is BookmarksUiEvent.OnArticleClick ->
                sendEffect(BookmarksUiEffect.NavigateToArticle(event.article))
            is BookmarksUiEvent.OnRemoveBookmark -> removeBookmark(event.articleId)
            BookmarksUiEvent.OnToggleSort -> {
                setState { copy(sortNewestFirst = !sortNewestFirst) }
                applyFilter()
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            getBookmarks().collect { articles ->
                setState { copy(bookmarks = articles, isLoading = false) }
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val all = uiState.value.bookmarks
        val filtered = when (uiState.value.selectedFilter) {
            BookmarkFilter.All -> all
            BookmarkFilter.Articles -> all
            BookmarkFilter.Offline -> all
        }
        setState { copy(filteredBookmarks = filtered) }
    }

    private fun removeBookmark(articleId: String) {
        viewModelScope.launch {
            removeBookmark.invoke(articleId)
        }
    }
}