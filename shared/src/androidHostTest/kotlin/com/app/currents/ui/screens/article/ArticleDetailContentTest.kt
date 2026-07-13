package com.app.currents.ui.screens.article

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.presentation.article.ArticleUiState
import com.app.currents.ui.theme.CurrentsTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticleDetailContentTest {

    private val article = Article(
        id = "1",
        title = "Global leaders reach historic accord on emissions",
        description = "Delegates reached an agreement after two weeks of talks.",
        content = null,
        url = "https://example.com/article",
        imageUrl = null,
        source = "BBC News",
        author = "Sarah Mitchell",
        category = Category.World,
        publishedAt = "2h ago",
    )

    @OptIn(ExperimentalTestApi::class)
    private fun setArticleContent(
        uiState: ArticleUiState = ArticleUiState(article = article),
        onBack: () -> Unit = {},
        onBookmarkToggle: () -> Unit = {},
        onShare: () -> Unit = {},
        onOpenBrowser: () -> Unit = {},
        content: androidx.compose.ui.test.ComposeUiTest,
    ) {
        content.setContent {
            CurrentsTheme(darkTheme = true) {
                ArticleDetailContent(
                    uiState = uiState,
                    onBack = onBack,
                    onBookmarkToggle = onBookmarkToggle,
                    onShare = onShare,
                    onOpenBrowser = onOpenBrowser,
                )
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun renders_title_source_and_author() = runComposeUiTest {
        setArticleContent(content = this)

        onNodeWithText(article.title).assertIsDisplayed()
        onNodeWithText(article.source).assertIsDisplayed()
        onNodeWithText("By ${article.author}").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_back_triggers_onBack() = runComposeUiTest {
        var backClicked = false
        setArticleContent(onBack = { backClicked = true }, content = this)

        onNodeWithContentDescription("Back").performClick()

        assert(backClicked) { "Expected onBack to be invoked" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_share_triggers_onShare() = runComposeUiTest {
        var shareClicked = false
        setArticleContent(onShare = { shareClicked = true }, content = this)

        onAllNodesWithContentDescription("Share").onFirst().performClick()

        assert(shareClicked) { "Expected onShare to be invoked" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_bookmark_triggers_onBookmarkToggle() = runComposeUiTest {
        var toggled = false
        setArticleContent(onBookmarkToggle = { toggled = true }, content = this)

        onAllNodesWithContentDescription("Bookmark").onFirst().performClick()

        assert(toggled) { "Expected onBookmarkToggle to be invoked" }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicking_open_in_browser_triggers_onOpenBrowser() = runComposeUiTest {
        var opened = false
        setArticleContent(onOpenBrowser = { opened = true }, content = this)

        onNodeWithText("Open in browser").performClick()

        assert(opened) { "Expected onOpenBrowser to be invoked" }
    }
}
