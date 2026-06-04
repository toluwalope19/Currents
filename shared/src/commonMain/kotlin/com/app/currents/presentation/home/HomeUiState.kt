package com.app.currents.presentation.home

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val selectedCategory: Category = Category.Top,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface HomeUiEvent : UiEvent {
    data class OnCategorySelected(val category: Category) : HomeUiEvent
    data class OnArticleClicked(val article: Article) : HomeUiEvent
    data object OnRefresh : HomeUiEvent
    data object OnRetry : HomeUiEvent
}

sealed interface HomeUiEffect : UiEffect {
    data class NavigateToArticle(val article: Article) : HomeUiEffect
}