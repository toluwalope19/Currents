package com.app.currents.presentation.article

import com.app.currents.domain.model.Article
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class ArticleUiState(
    val article: Article? = null,
    val isBookmarked: Boolean = false,
    val aiSummary: String? = null,
    val isSummaryLoading: Boolean = false,
    val isSummaryError: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface ArticleUiEvent : UiEvent {
    data class OnArticleLoaded(val article: Article) : ArticleUiEvent
    data object OnBookmarkToggled : ArticleUiEvent
    data object OnShareClicked : ArticleUiEvent
    data object OnReadFullArticleClicked : ArticleUiEvent
    data object OnRetrySummary : ArticleUiEvent
    data object OnBack : ArticleUiEvent
}

sealed interface ArticleUiEffect : UiEffect {
    data class OpenUrl(val url: String) : ArticleUiEffect
    data class ShareArticle(val title: String, val url: String) : ArticleUiEffect
    data object NavigateBack : ArticleUiEffect
}