package com.app.currents.wear.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.GetBookmarksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class SavedUiState(
    val bookmarks: List<Article> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class SavedViewModel(
    private val getBookmarks: GetBookmarksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        getBookmarks()
            .onEach { bookmarks ->
                _uiState.value = SavedUiState(
                    bookmarks = bookmarks,
                    isLoading = false,
                )
            }
            .catch { e ->
                _uiState.value = SavedUiState(
                    isLoading = false,
                    error = e.message,
                )
            }
            .launchIn(viewModelScope)
    }
}