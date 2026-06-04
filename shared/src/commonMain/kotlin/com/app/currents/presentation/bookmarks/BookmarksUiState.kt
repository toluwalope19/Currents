package com.app.currents.presentation.bookmarks

import com.app.currents.domain.model.Article
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class BookmarksUiState(
    val bookmarks: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
) : UiState

sealed interface BookmarksUiEvent : UiEvent {
    data class OnArticleClicked(val article: Article) : BookmarksUiEvent
    data class OnRemoveBookmark(val article: Article) : BookmarksUiEvent
}

sealed interface BookmarksUiEffect : UiEffect {
    data class NavigateToArticle(val article: Article) : BookmarksUiEffect
    data class ShowUndoSnackbar(val article: Article) : BookmarksUiEffect
}