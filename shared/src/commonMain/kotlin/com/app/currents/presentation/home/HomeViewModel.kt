package com.app.currents.presentation.home

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.domain.usecase.GetFeedUseCase
import com.app.currents.domain.usecase.RefreshFeedUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.domain.usecase.HasCachedArticlesUseCase
import com.app.currents.util.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeedUseCase: GetFeedUseCase,
    private val getByCategoryUseCase: GetByCategoryUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val hasCachedArticlesUseCase: HasCachedArticlesUseCase,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<HomeUiState, HomeUiEvent, HomeUiEffect>(HomeUiState()) {

    private var feedJob: Job? = null
    private var hasLoadedOnce = false


    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                setState { copy(isOffline = !online) }
            }
        }
    }

    init {
        observeNetwork()
        viewModelScope.launch {
            val hasCached = hasCachedArticlesUseCase()
            setState { copy(isLoading = !hasCached) }
            loadFeed(Category.Top)
        }
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnCategorySelected -> {
                setState { copy(selectedCategory = event.category) }
                loadFeed(event.category)
            }
            is HomeUiEvent.OnArticleClicked -> {
                sendEffect(HomeUiEffect.NavigateToArticle(event.article))
            }
            HomeUiEvent.OnRefresh -> refresh()
            HomeUiEvent.OnRetry -> loadFeed(uiState.value.selectedCategory)
        }
    }

    private fun loadFeed(category: Category) {
        feedJob?.cancel()

        val flow = if (category == Category.Top) {
            getFeedUseCase()
        } else {
            getByCategoryUseCase(category)
        }

        feedJob = flow
            .onEach { articles ->
                setState {
                    copy(
                        articles = articles,
                        isLoading = false,
                        isOffline = false,
                        error = null,
                    )
                }
            }
            .catch { throwable ->
                setState {
                    copy(
                        isLoading = false,
                        isOffline = articles.isNotEmpty(),
                        error = if (articles.isEmpty()) throwable.message else null,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            refreshFeedUseCase(uiState.value.selectedCategory)
            setState { copy(isRefreshing = false) }
        }
    }
}