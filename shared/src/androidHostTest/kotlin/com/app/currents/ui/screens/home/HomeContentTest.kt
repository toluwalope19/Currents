package com.app.currents.ui.screens.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.home.HomeUiEvent
import com.app.currents.presentation.home.HomeUiState
import com.app.currents.ui.theme.CurrentsTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeContentTest {

    private fun article(id: String, title: String) = Article(
        id = id,
        title = title,
        description = "",
        content = null,
        url = "",
        imageUrl = null,
        source = "Reuters",
        author = null,
        category = Category.Top,
        publishedAt = "2h ago",
    )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_hero_article_and_top_stories_list() = runComposeUiTest {
        val hero = article("1", "Hero headline about the economy")
        val second = article("2", "Second story headline")
        setContent {
            CurrentsTheme(darkTheme = true) {
                HomeContent(
                    uiState = HomeUiState(articles = listOf(hero, second)),
                    isDarkTheme = true,
                    onThemeToggle = {},
                    onEvent = {},
                )
            }
        }

        onNodeWithText(hero.title).assertIsDisplayed()
        onNodeWithText("Top Stories").assertIsDisplayed()
        onNodeWithText(second.title).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_offline_banner_when_offline() = runComposeUiTest {
        setContent {
            CurrentsTheme(darkTheme = true) {
                HomeContent(
                    uiState = HomeUiState(isOffline = true),
                    isDarkTheme = true,
                    onThemeToggle = {},
                    onEvent = {},
                )
            }
        }

        onNodeWithText("You're offline — showing cached content").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_a_list_article_sends_onArticleClicked() = runComposeUiTest {
        val hero = article("1", "Hero headline about the economy")
        val second = article("2", "Second story headline")
        var clicked: Article? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                HomeContent(
                    uiState = HomeUiState(articles = listOf(hero, second)),
                    isDarkTheme = true,
                    onThemeToggle = {},
                    onEvent = { event ->
                        if (event is HomeUiEvent.OnArticleClicked) clicked = event.article
                    },
                )
            }
        }

        onNodeWithText(second.title).performClick()

        assert(clicked == second) { "Expected click on '${second.title}' to report OnArticleClicked, got $clicked" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_error_message_when_error_set() = runComposeUiTest {
        setContent {
            CurrentsTheme(darkTheme = true) {
                HomeContent(
                    uiState = HomeUiState(error = "Couldn't load your feed"),
                    isDarkTheme = true,
                    onThemeToggle = {},
                    onEvent = {},
                )
            }
        }

        onNodeWithText("Couldn't load your feed").assertIsDisplayed()
    }
}
