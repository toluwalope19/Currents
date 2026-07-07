package com.app.currents.wear.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class FeedUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class FeedViewModel(
    private val getFeed: GetFeedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        getFeed()
            .onEach { articles ->
                _uiState.value = FeedUiState(
                    articles = articles,
                    isLoading = false,
                )
            }
            .catch { e ->
                _uiState.value = FeedUiState(
                    isLoading = false,
                    error = e.message,
                )
            }
            .launchIn(viewModelScope)
    }
}