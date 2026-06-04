package com.app.currents.presentation.article

import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.IsBookmarkedUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val removeBookmarkUseCase: RemoveBookmarkUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
) : BaseViewModel<ArticleUiState, ArticleUiEvent, ArticleUiEffect>(ArticleUiState()) {

    override fun onEvent(event: ArticleUiEvent) {
        when (event) {
            is ArticleUiEvent.OnArticleLoaded -> loadArticle(event.article)
            ArticleUiEvent.OnBookmarkToggled -> toggleBookmark()
            ArticleUiEvent.OnShareClicked -> shareArticle()
            ArticleUiEvent.OnReadFullArticleClicked -> openFullArticle()
            ArticleUiEvent.OnRetrySummary -> retrySummary()
            ArticleUiEvent.OnBack -> sendEffect(ArticleUiEffect.NavigateBack)
        }
    }

    private fun loadArticle(article: Article) {
        viewModelScope.launch {
            val isBookmarked = isBookmarkedUseCase(article.id)
            setState { copy(article = article, isBookmarked = isBookmarked) }
            fetchSummary(article)
        }
    }

    private fun fetchSummary(article: Article) {
        // AI summary via Claude API — wired in later
        // For now we use the article description as a placeholder
        setState {
            copy(
                aiSummary = article.description.ifBlank { null },
                isSummaryLoading = false,
                isSummaryError = false,
            )
        }
    }

    private fun toggleBookmark() {
        val article = uiState.value.article ?: return
        viewModelScope.launch {
            if (uiState.value.isBookmarked) {
                removeBookmarkUseCase(article.id)
                setState { copy(isBookmarked = false) }
            } else {
                addBookmarkUseCase(article)
                setState { copy(isBookmarked = true) }
            }
        }
    }

    private fun shareArticle() {
        val article = uiState.value.article ?: return
        sendEffect(ArticleUiEffect.ShareArticle(article.title, article.url))
    }

    private fun openFullArticle() {
        val url = uiState.value.article?.url ?: return
        sendEffect(ArticleUiEffect.OpenUrl(url))
    }

    private fun retrySummary() {
        val article = uiState.value.article ?: return
        setState { copy(isSummaryLoading = true, isSummaryError = false) }
        fetchSummary(article)
    }
}