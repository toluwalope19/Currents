package com.app.currents.ui.screens.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.search.SearchUiEvent
import com.app.currents.presentation.search.SearchUiState
import com.app.currents.presentation.search.TrendingTopic
import com.app.currents.ui.theme.CurrentsTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchContentTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun selecting_a_scope_tab_sends_onScopeChange() = runComposeUiTest {
        var event: SearchUiEvent? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                SearchContent(uiState = SearchUiState(), onEvent = { event = it })
            }
        }

        onNodeWithText("People").performClick()

        assert(event == SearchUiEvent.OnScopeChange(com.app.currents.presentation.search.SearchScope.People)) {
            "Expected OnScopeChange(People), got $event"
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_a_trending_topic_sends_onTrendingClick() = runComposeUiTest {
        val topic = TrendingTopic(title = "Gaza Ceasefire", source = "BBC News", category = Category.Politics)
        var event: SearchUiEvent? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                SearchContent(
                    uiState = SearchUiState(trending = listOf(topic)),
                    onEvent = { event = it },
                )
            }
        }

        onNodeWithText(topic.title).performClick()

        assert(event == SearchUiEvent.OnTrendingClick(topic)) { "Expected OnTrendingClick($topic), got $event" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_a_result_sends_onResultClick() = runComposeUiTest {
        val article = Article(
            id = "1",
            title = "OpenAI releases GPT-5 with major reasoning improvements",
            description = "",
            content = null,
            url = "",
            imageUrl = null,
            source = "The Verge",
            author = null,
            category = Category.Technology,
            publishedAt = "2026-06-29",
        )
        var event: SearchUiEvent? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                SearchContent(
                    uiState = SearchUiState(query = "AI", hasSearched = true, results = listOf(article)),
                    onEvent = { event = it },
                )
            }
        }

        onNodeWithText(article.title).performClick()

        assert(event == SearchUiEvent.OnResultClick(article)) { "Expected OnResultClick, got $event" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_no_results_message_when_search_returns_nothing() = runComposeUiTest {
        setContent {
            CurrentsTheme(darkTheme = true) {
                SearchContent(
                    uiState = SearchUiState(query = "xyznotfound", hasSearched = true, results = emptyList()),
                    onEvent = {},
                )
            }
        }

        onNodeWithText("No results").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_error_message_when_search_fails() = runComposeUiTest {
        setContent {
            CurrentsTheme(darkTheme = true) {
                SearchContent(
                    uiState = SearchUiState(query = "AI", hasSearched = true, error = "Something went wrong"),
                    onEvent = {},
                )
            }
        }

        onNodeWithText("Couldn't search").assertIsDisplayed()
    }
}
