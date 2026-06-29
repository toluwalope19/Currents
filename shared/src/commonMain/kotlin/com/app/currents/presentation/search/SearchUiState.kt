package com.app.currents.presentation.search

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class SearchUiState(
    val query: String = "",
    val scope: SearchScope = SearchScope.All,
    val isSearching: Boolean = false,
    val results: List<Article> = emptyList(),
    val hasSearched: Boolean = false,
    val error: String? = null,
    val trending: List<TrendingTopic> = emptyList(),
    val topics: List<TopicSelectable> = emptyList(),
): UiState {
    val showResults: Boolean get() = query.isNotBlank()
    val selectedCount: Int get() = topics.count { it.isSelected }
}

enum class SearchScope(val label: String) {
    All("All"), People("People"), Topics("Topics"), Sources("Sources")
}

data class TrendingTopic(val title: String, val source: String, val category: Category,val imageUrl: String? = null)

data class TopicSelectable(val category: Category, val isSelected: Boolean)

sealed interface SearchUiEvent: UiEvent {
    data class OnQueryChange(val query: String) : SearchUiEvent
    data class OnScopeChange(val scope: SearchScope) : SearchUiEvent
    data object OnClearQuery : SearchUiEvent
    data object OnVoiceClick : SearchUiEvent
    data class OnTrendingClick(val topic: TrendingTopic) : SearchUiEvent
    data class OnTopicToggle(val category: Category) : SearchUiEvent
    data class OnResultClick(val article: Article) : SearchUiEvent
}

sealed interface SearchUiEffect: UiEffect {
    data class NavigateToArticle(val article: Article) : SearchUiEffect
    data object StartVoiceInput : SearchUiEffect
}