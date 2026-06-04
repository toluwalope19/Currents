package com.app.currents.presentation.search

import com.app.currents.domain.usecase.SearchArticlesUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchArticlesUseCase: SearchArticlesUseCase,
) : BaseViewModel<SearchUiState, SearchUiEvent, SearchUiEffect>(SearchUiState()) {

    private var searchJob: Job? = null

    override fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChanged -> onQueryChanged(event.query)
            is SearchUiEvent.OnArticleClicked -> {
                sendEffect(SearchUiEffect.NavigateToArticle(event.article))
            }
            SearchUiEvent.OnClearQuery -> {
                searchJob?.cancel()
                setState {
                    copy(
                        query = "",
                        results = emptyList(),
                        isLoading = false,
                        isIdle = true,
                        isEmpty = false,
                        error = null,
                    )
                }
            }
        }
    }

    private fun onQueryChanged(query: String) {
        setState { copy(query = query) }

        if (query.isBlank()) {
            searchJob?.cancel()
            setState {
                copy(
                    results = emptyList(),
                    isLoading = false,
                    isIdle = true,
                    isEmpty = false,
                    error = null,
                )
            }
            return
        }

        // debounce — wait 400ms after user stops typing before firing network call
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            setState { copy(isLoading = true, isIdle = false, error = null) }
            delay(400)
            searchArticlesUseCase(query)
                .onSuccess { articles ->
                    setState {
                        copy(
                            results = articles,
                            isLoading = false,
                            isEmpty = articles.isEmpty(),
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    setState {
                        copy(
                            isLoading = false,
                            isEmpty = false,
                            error = throwable.message,
                        )
                    }
                }
        }
    }
}