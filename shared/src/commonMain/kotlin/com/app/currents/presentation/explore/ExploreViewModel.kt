package com.app.currents.presentation.explore

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.usecase.GetByCategoryUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val getByCategoryUseCase: GetByCategoryUseCase,
) : BaseViewModel<ExploreUiState, ExploreUiEvent, ExploreUiEffect>(ExploreUiState()) {

    private var categoryJob: Job? = null

    init {
        loadExplore()
    }

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

    // Load multiple categories in parallel and combine — fills the mosaic grid
    private fun loadExplore() {
        categoryJob?.cancel()
        setState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val categories = listOf(
                    Category.Technology,
                    Category.Sports,
                    Category.Business,
                    Category.World,
                    Category.Entertainment,
                    Category.Health,
                    Category.Science,
                )

                val results = categories.map { category ->
                    async {
                        getByCategoryUseCase(category)
                            .catch { emit(emptyList()) }
                            .first()
                            .take(5) // 2 articles per category
                    }
                }.awaitAll()
                    .flatten()
                    .distinctBy { it.id }
                    .shuffled()

                setState { copy(articles = results, isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
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