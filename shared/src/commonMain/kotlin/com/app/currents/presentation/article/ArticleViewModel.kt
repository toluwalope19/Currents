package com.app.currents.presentation.article

import com.app.currents.domain.model.Article
import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.IsBookmarkedUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.app.currents.di.AppConfig
import com.app.currents.domain.usecase.GetArticleByIdUseCase
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


@kotlinx.serialization.Serializable
private data class ClaudeRequest(
    val model: String,
    val max_tokens: Int,
    val messages: List<ClaudeMessage>,
)

@kotlinx.serialization.Serializable
private data class ClaudeMessage(
    val role: String,
    val content: String,
)

@kotlinx.serialization.Serializable
private data class ClaudeResponse(
    val content: List<ClaudeContent>,
)

@Serializable
private data class ClaudeContent(
    val text: String,
)

class ArticleViewModel(
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val removeBookmarkUseCase: RemoveBookmarkUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val getArticleByIdUseCase: GetArticleByIdUseCase,
    private val appConfig: AppConfig,
    private val httpClient: HttpClient = HttpClient(),
) : BaseViewModel<ArticleUiState, ArticleUiEvent, ArticleUiEffect>(ArticleUiState()) {

    override fun onEvent(event: ArticleUiEvent) {
        when (event) {
            is ArticleUiEvent.OnArticleLoaded -> loadArticle(event.article)
            is ArticleUiEvent.OnArticleLoadedById -> loadArticleById(event.articleId)
            ArticleUiEvent.OnBookmarkToggled -> toggleBookmark()
            ArticleUiEvent.OnShareClicked -> shareArticle()
            ArticleUiEvent.OnReadFullArticleClicked -> openFullArticle()
            ArticleUiEvent.OnRetrySummary -> retrySummary()
            ArticleUiEvent.OnBack -> sendEffect(ArticleUiEffect.NavigateBack)
        }
    }

    private fun loadArticleById(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val article = getArticleByIdUseCase(id)
            if (article != null) {
                val bookmarked = isBookmarkedUseCase(article.id)
                setState { copy(article = article, isBookmarked = bookmarked, isLoading = false) }
                fetchAiSummary()
            } else {
                setState { copy(isLoading = false, error = "Article not found") }
            }
        }
    }

    private fun loadArticle(article: Article) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val bookmarked = isBookmarkedUseCase(article.id)
            setState { copy(article = article, isBookmarked = bookmarked, isLoading = false) }
            fetchAiSummary()
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

    private fun fetchAiSummary() {
        val article = uiState.value.article ?: return
        viewModelScope.launch {
            setState { copy(isSummaryLoading = true, aiSummary = null) }
            try {
                val client = httpClient
                val prompt = """
                    Summarize this news article in 2-3 concise sentences.
                    Be factual and objective. Do not start with "This article" or "The article".
                    
                    Title: ${article.title}
                    Description: ${article.description}
                """.trimIndent()

                val requestBody = Json.encodeToString(
                    ClaudeRequest.serializer(),
                    ClaudeRequest(
                        model = "claude-haiku-4-5-20251001",
                        max_tokens = 200,
                        messages = listOf(
                            ClaudeMessage(role = "user", content = prompt)
                        ),
                    )
                )

                val response = client.post("https://api.anthropic.com/v1/messages") {
                    contentType(ContentType.Application.Json)
                    header("x-api-key", appConfig.claudeApiKey)
                    header("anthropic-version", "2023-06-01")
                    setBody(requestBody)
                }

                val responseText = response.body<String>()
                println("AI SUMMARY RAW: $responseText")

                val parsed = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }.decodeFromString(ClaudeResponse.serializer(), responseText)


                val summary = parsed.content.firstOrNull()?.text?.trim()
                println("AI SUMMARY SUCCESS: $summary")
                setState {
                    copy(
                        aiSummary = summary,
                        isSummaryLoading = false,
                    )
                }
            } catch (e: Exception) {
                println("AI SUMMARY ERROR: ${e.message}")
                setState {
                    copy(
                        aiSummary = article.description,
                        isSummaryLoading = false,
                    )
                }
            }
        }
    }


}