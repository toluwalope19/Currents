package com.app.currents.ui.bookmarks

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.bookmarks.BookmarkFilter
import com.app.currents.presentation.bookmarks.BookmarksUiEvent
import com.app.currents.presentation.bookmarks.BookmarksUiState
import com.app.currents.ui.theme.CurrentsTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BookmarksContentTest {

    private val article = Article(
        id = "1",
        title = "Global leaders reach historic accord at climate summit",
        description = "",
        content = null,
        url = "",
        imageUrl = null,
        source = "The Guardian",
        author = null,
        category = Category.World,
        publishedAt = "2h ago",
        isBookmarked = true,
    )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_bookmarked_article() = runComposeUiTest {
        val state = BookmarksUiState(bookmarks = listOf(article), filteredBookmarks = listOf(article))
        setContent {
            CurrentsTheme(darkTheme = true) {
                BookmarksContent(uiState = state, onEvent = {})
            }
        }

        onNodeWithText(article.title).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shows_empty_state_when_no_bookmarks() = runComposeUiTest {
        setContent {
            CurrentsTheme(darkTheme = true) {
                BookmarksContent(uiState = BookmarksUiState(), onEvent = {})
            }
        }

        onNodeWithText("No saved articles").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun selecting_a_filter_sends_onFilterSelected() = runComposeUiTest {
        var event: BookmarksUiEvent? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                BookmarksContent(uiState = BookmarksUiState(), onEvent = { event = it })
            }
        }

        // "Articles" is used over "Offline"/"Saved" since those collide with the stat row labels above.
        onNodeWithText(BookmarkFilter.Articles.label).performClick()

        assert(event == BookmarksUiEvent.OnFilterSelected(BookmarkFilter.Articles)) {
            "Expected OnFilterSelected(Articles), got $event"
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_a_bookmark_sends_onArticleClick() = runComposeUiTest {
        val state = BookmarksUiState(bookmarks = listOf(article), filteredBookmarks = listOf(article))
        var event: BookmarksUiEvent? = null
        setContent {
            CurrentsTheme(darkTheme = true) {
                BookmarksContent(uiState = state, onEvent = { event = it })
            }
        }

        onNodeWithText(article.title).performClick()

        assert(event == BookmarksUiEvent.OnArticleClick(article)) { "Expected OnArticleClick, got $event" }
    }
}
