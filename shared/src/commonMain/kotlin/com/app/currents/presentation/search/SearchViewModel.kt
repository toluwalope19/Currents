package com.app.currents.presentation.search

import androidx.lifecycle.viewModelScope
import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetFeedUseCase
import com.app.currents.domain.usecase.SearchArticlesUseCase
import com.app.currents.presentation.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchArticles: SearchArticlesUseCase,
    private val getFeed: GetFeedUseCase,
) : BaseViewModel<SearchUiState, SearchUiEvent, SearchUiEffect>(
     SearchUiState(),
) {

    private val queryFlow = MutableStateFlow("")

    init {
        loadTopics()
        loadTrending()
        observeQuery()
    }

    override fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChange -> {
                setState { copy(query = event.query) }
                queryFlow.value = event.query
            }
            is SearchUiEvent.OnScopeChange -> {
                setState { copy(scope = event.scope) }
                queryFlow.value = uiState.value.query
            }
            SearchUiEvent.OnClearQuery -> {
                setState { copy(query = "", results = emptyList(), hasSearched = false, error = null) }
                queryFlow.value = ""
            }
            SearchUiEvent.OnVoiceClick -> sendEffect(SearchUiEffect.StartVoiceInput)
            is SearchUiEvent.OnTrendingClick -> {
                setState { copy(query = event.topic.title) }
                queryFlow.value = event.topic.title
            }
            is SearchUiEvent.OnTopicToggle -> toggleTopic(event.category)
            is SearchUiEvent.OnResultClick -> sendEffect(SearchUiEffect.NavigateToArticle(event.article))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .map { it.trim() }
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isEmpty()) {
                        flowOf<SearchPartial>(SearchPartial.Idle)
                    } else flow {
                        emit(SearchPartial.Loading)
                        searchArticles(q)
                            .onSuccess { emit(SearchPartial.Success(it)) }
                            .onFailure { emit(SearchPartial.Error(it.message ?: "Something went wrong")) }
                    }
                }
                .onEach { partial ->
                    when (partial) {
                        SearchPartial.Idle -> setState {
                            copy(isSearching = false, results = emptyList(), hasSearched = false, error = null)
                        }
                        SearchPartial.Loading -> setState {
                            copy(isSearching = true, error = null)
                        }
                        is SearchPartial.Success -> setState {
                            copy(isSearching = false, results = partial.articles, hasSearched = true, error = null)
                        }
                        is SearchPartial.Error -> setState {
                            copy(isSearching = false, results = emptyList(), hasSearched = true, error = partial.message)
                        }
                    }
                }
                .collect {}
        }
    }

    private fun loadTrending() {
        viewModelScope.launch {
            getFeed()
                .take(1)  // just need one emission — no need to stay subscribed
                .collect { articles ->
                    setState {
                        copy(
                            trending = articles.take(6).map { article ->
                                TrendingTopic(
                                    title = article.title,
                                    source = article.source,
                                    category = article.category,
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun loadTopics() {
        val defaults = setOf(Category.Technology, Category.Sports)
        setState {
            copy(topics = Category.onboardingTopics.map {
                TopicSelectable(it, isSelected = it in defaults)
            })
        }
    }

    private fun toggleTopic(category: Category) {
        setState {
            copy(topics = topics.map {
                if (it.category == category) it.copy(isSelected = !it.isSelected) else it
            })
        }
        // TODO: persist via DataStore (same as Onboarding)
    }

    private sealed interface SearchPartial {
        data object Idle : SearchPartial
        data object Loading : SearchPartial
        data class Success(val articles: List<com.app.currents.domain.model.Article>) : SearchPartial
        data class Error(val message: String) : SearchPartial
    }
}