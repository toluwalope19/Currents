package com.app.currents.presentation.search

import com.app.currents.domain.model.Article
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class SearchUiState(
    val query: String = "",
    val results: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isIdle: Boolean = true,      // true when search bar is empty — shows placeholder
    val isEmpty: Boolean = false,    // true when query returned no results
    val error: String? = null,
) : UiState

sealed interface SearchUiEvent : UiEvent {
    data class OnQueryChanged(val query: String) : SearchUiEvent
    data class OnArticleClicked(val article: Article) : SearchUiEvent
    data object OnClearQuery : SearchUiEvent
}

sealed interface SearchUiEffect : UiEffect {
    data class NavigateToArticle(val article: Article) : SearchUiEffect
}