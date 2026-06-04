package com.app.currents.presentation.explore

import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ExploreViewModel(
    private val getByCategoryUseCase: GetByCategoryUseCase,
) : BaseViewModel<ExploreUiState, ExploreUiEvent, ExploreUiEffect>(ExploreUiState()) {

    private var categoryJob: Job? = null

    override fun onEvent(event: ExploreUiEvent) {
        when (event) {
            is ExploreUiEvent.OnCategoryClicked -> loadCategory(event.category)
            is ExploreUiEvent.OnArticleClicked -> {
                sendEffect(ExploreUiEffect.NavigateToArticle(event.article))
            }
            ExploreUiEvent.OnBackFromCategory -> {
                categoryJob?.cancel()
                setState { copy(selectedCategory = null, articles = emptyList(), error = null) }
            }
        }
    }

    private fun loadCategory(category: Category) {
        categoryJob?.cancel()
        setState { copy(selectedCategory = category, isLoading = true, error = null) }

        categoryJob = getByCategoryUseCase(category)
            .onEach { articles ->
                setState { copy(articles = articles, isLoading = false, error = null) }
            }
            .catch { throwable ->
                setState { copy(isLoading = false, error = throwable.message) }
            }
            .launchIn(viewModelScope)
    }
}