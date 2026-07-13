package com.app.currents.presentation.article

import com.app.currents.di.AppConfig
import com.app.currents.domain.usecase.AddBookmarkUseCase
import com.app.currents.domain.usecase.GetArticleByIdUseCase
import com.app.currents.domain.usecase.IsBookmarkedUseCase
import com.app.currents.domain.usecase.RemoveBookmarkUseCase
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeBookmarkRepository
import com.app.currents.fakes.runViewModelTest
import com.app.currents.fakes.testArticle
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArticleViewModelTest {

    // MockEngine defaults to Dispatchers.IO for request execution, which would hop the
    // fetchAiSummary coroutine off the virtual test dispatcher onto a real thread. Pinning it
    // to Unconfined keeps everything on the TestCoroutineScheduler so advanceUntilIdle() can
    // drive it deterministically.
    private fun mockClient(responseBody: String, status: HttpStatusCode = HttpStatusCode.OK) = HttpClient(MockEngine) {
        engine {
            dispatcher = Dispatchers.Unconfined
            addHandler { _ ->
                respond(
                    content = responseBody,
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        }
    }

    private fun buildViewModel(
        local: FakeArticleLocalRepository = FakeArticleLocalRepository(),
        bookmarks: FakeBookmarkRepository = FakeBookmarkRepository(),
        httpClient: HttpClient = mockClient("""{"content":[{"text":"AI summary."}]}"""),
    ): ArticleViewModel = ArticleViewModel(
        addBookmarkUseCase = AddBookmarkUseCase(bookmarks),
        removeBookmarkUseCase = RemoveBookmarkUseCase(bookmarks),
        isBookmarkedUseCase = IsBookmarkedUseCase(bookmarks),
        getArticleByIdUseCase = GetArticleByIdUseCase(local),
        appConfig = AppConfig(newsApiKey = "news-key", claudeApiKey = "claude-key"),
        httpClient = httpClient,
    )

    @Test
    fun loads_article_by_id_and_marks_bookmarked_state() = runViewModelTest {
        val article = testArticle(id = "1")
        val local = FakeArticleLocalRepository(initial = listOf(article))
        val bookmarks = FakeBookmarkRepository(initial = listOf(article))
        val viewModel = buildViewModel(local = local, bookmarks = bookmarks)

        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("1"))
        advanceUntilIdle()

        assertEquals(article, viewModel.uiState.value.article)
        assertTrue(viewModel.uiState.value.isBookmarked)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun sets_error_when_article_id_not_found() = runViewModelTest {
        val viewModel = buildViewModel(local = FakeArticleLocalRepository())

        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("missing"))
        advanceUntilIdle()

        assertEquals("Article not found", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.article)
    }

    @Test
    fun fetches_ai_summary_after_loading_article() = runViewModelTest {
        val article = testArticle(id = "1")
        val viewModel = buildViewModel(
            local = FakeArticleLocalRepository(initial = listOf(article)),
            httpClient = mockClient("""{"content":[{"text":"A concise AI summary."}]}"""),
        )

        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("1"))
        advanceUntilIdle()

        assertEquals("A concise AI summary.", viewModel.uiState.value.aiSummary)
        assertEquals(false, viewModel.uiState.value.isSummaryLoading)
    }

    @Test
    fun falls_back_to_description_when_ai_summary_request_fails() = runViewModelTest {
        val article = testArticle(id = "1", description = "Fallback description")
        val viewModel = buildViewModel(
            local = FakeArticleLocalRepository(initial = listOf(article)),
            httpClient = mockClient("not valid json", status = HttpStatusCode.InternalServerError),
        )

        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("1"))
        advanceUntilIdle()

        assertEquals("Fallback description", viewModel.uiState.value.aiSummary)
    }

    @Test
    fun toggle_bookmark_adds_then_removes_article() = runViewModelTest {
        val article = testArticle(id = "1")
        val local = FakeArticleLocalRepository(initial = listOf(article))
        val bookmarks = FakeBookmarkRepository()
        val viewModel = buildViewModel(local = local, bookmarks = bookmarks)
        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("1"))
        advanceUntilIdle()

        viewModel.onEvent(ArticleUiEvent.OnBookmarkToggled)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isBookmarked)
        assertEquals(listOf(article), bookmarks.bookmarksState.value)

        viewModel.onEvent(ArticleUiEvent.OnBookmarkToggled)
        advanceUntilIdle()
        assertEquals(false, viewModel.uiState.value.isBookmarked)
        assertEquals(emptyList(), bookmarks.bookmarksState.value)
    }

    @Test
    fun share_clicked_sends_share_effect_with_title_and_url() = runViewModelTest {
        val article = testArticle(id = "1", title = "My Title")
        val local = FakeArticleLocalRepository(initial = listOf(article))
        val viewModel = buildViewModel(local = local)
        viewModel.onEvent(ArticleUiEvent.OnArticleLoadedById("1"))
        advanceUntilIdle()

        viewModel.onEvent(ArticleUiEvent.OnShareClicked)
        advanceUntilIdle()

        assertEquals(ArticleUiEffect.ShareArticle("My Title", article.url), viewModel.effect.first())
    }

    @Test
    fun back_event_sends_navigate_back_effect() = runViewModelTest {
        val viewModel = buildViewModel()

        viewModel.onEvent(ArticleUiEvent.OnBack)
        advanceUntilIdle()

        assertEquals(ArticleUiEffect.NavigateBack, viewModel.effect.first())
    }
}
