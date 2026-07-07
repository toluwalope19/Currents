package com.app.currents.wear.presentation.article

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.GetArticleByIdUseCase
import com.app.currents.domain.usecase.IsBookmarkedUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WearArticleUiState(
    val article: Article? = null,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false,
    val isBookmarked: Boolean = false,
    val error: String? = null,
)

class WearArticleViewModel(
    private val tts: TextToSpeech,
    private val getArticleById: GetArticleByIdUseCase,
    private val addBookmark: AddBookmarkUseCase,
    private val removeBookmark: RemoveBookmarkUseCase,
    private val isBookmarked: IsBookmarkedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WearArticleUiState())
    val uiState: StateFlow<WearArticleUiState> = _uiState.asStateFlow()

    fun loadArticle(articleId: String) {
        viewModelScope.launch {
            val article = getArticleById(articleId)
            if (article != null) {
                val bookmarked = isBookmarked(article.id)
                _uiState.value = WearArticleUiState(
                    article = article,
                    isLoading = false,
                    isBookmarked = bookmarked,
                )
            } else {
                _uiState.value = WearArticleUiState(
                    isLoading = false,
                    error = "Article not found",
                )
            }
        }
    }

    fun readAloud() {
        val article = _uiState.value.article ?: return
        val text = "${article.title}. ${article.description}"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, article.id)
        _uiState.value = _uiState.value.copy(isSpeaking = true)
    }

    fun stopReading() {
        tts.stop()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    fun toggleBookmark() {
        val article = _uiState.value.article ?: return
        viewModelScope.launch {
            if (_uiState.value.isBookmarked) {
                removeBookmark(article.id)
                _uiState.value = _uiState.value.copy(isBookmarked = false)
            } else {
                addBookmark(article)
                _uiState.value = _uiState.value.copy(isBookmarked = true)
            }
        }
    }

    override fun onCleared() {
        tts.shutdown()
        super.onCleared()
    }
}