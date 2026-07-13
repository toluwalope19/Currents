package com.app.currents.domain.usecase

import com.app.currents.fakes.FakeBookmarkRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookmarkUseCasesTest {

    @Test
    fun getBookmarksUseCase_emits_current_bookmarks() = runTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val useCase = GetBookmarksUseCase(repository)

        assertEquals(listOf(article), useCase().first())
    }

    @Test
    fun addBookmarkUseCase_adds_article_to_repository() = runTest {
        val repository = FakeBookmarkRepository()
        val useCase = AddBookmarkUseCase(repository)
        val article = testArticle(id = "1")

        useCase(article)

        assertEquals(listOf(article), repository.bookmarksState.value)
    }

    @Test
    fun removeBookmarkUseCase_removes_article_from_repository() = runTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val useCase = RemoveBookmarkUseCase(repository)

        useCase(article.id)

        assertEquals(emptyList(), repository.bookmarksState.value)
    }

    @Test
    fun isBookmarkedUseCase_reflects_repository_state() = runTest {
        val article = testArticle(id = "1")
        val repository = FakeBookmarkRepository(initial = listOf(article))
        val useCase = IsBookmarkedUseCase(repository)

        assertTrue(useCase("1"))
        assertFalse(useCase("missing"))
    }
}
