package com.app.currents.presentation.explore

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class ExploreUiState(
    val categories: List<Category> = Category.all,
    val selectedCategory: Category? = null,
    val articles: List<Article> = emptyList(),
    val isOffline: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface ExploreUiEvent : UiEvent {
    data class OnCategoryClicked(val category: Category) : ExploreUiEvent
    data class OnArticleClicked(val article: Article) : ExploreUiEvent
    data object OnBackFromCategory : ExploreUiEvent
}

sealed interface ExploreUiEffect : UiEffect {
    data class NavigateToArticle(val article: Article) : ExploreUiEffect
}