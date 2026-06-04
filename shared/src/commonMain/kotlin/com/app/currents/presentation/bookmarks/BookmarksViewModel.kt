package com.app.currents.presentation.bookmarks

import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.GetBookmarksUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val removeBookmarkUseCase: RemoveBookmarkUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
) : BaseViewModel<BookmarksUiState, BookmarksUiEvent, BookmarksUiEffect>(BookmarksUiState()) {

    init {
        observeBookmarks()
    }

    override fun onEvent(event: BookmarksUiEvent) {
        when (event) {
            is BookmarksUiEvent.OnArticleClicked -> {
                sendEffect(BookmarksUiEffect.NavigateToArticle(event.article))
            }
            is BookmarksUiEvent.OnRemoveBookmark -> removeBookmark(event.article)
        }
    }

    private fun observeBookmarks() {
        getBookmarksUseCase()
            .onStart { setState { copy(isLoading = true) } }
            .onEach { bookmarks ->
                setState {
                    copy(
                        bookmarks = bookmarks,
                        isLoading = false,
                        isEmpty = bookmarks.isEmpty(),
                    )
                }
            }
            .catch { setState { copy(isLoading = false) } }
            .launchIn(viewModelScope)
    }

    private fun removeBookmark(article: Article) {
        viewModelScope.launch {
            removeBookmarkUseCase(article.id)
            // send undo snackbar — user can re-add within a few seconds
            sendEffect(BookmarksUiEffect.ShowUndoSnackbar(article))
        }
    }

    fun undoRemove(article: Article) {
        viewModelScope.launch {
            addBookmarkUseCase(article)
        }
    }
}