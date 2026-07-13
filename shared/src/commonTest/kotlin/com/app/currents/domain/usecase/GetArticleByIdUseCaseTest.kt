package com.app.currents.domain.usecase

import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetArticleByIdUseCaseTest {

    @Test
    fun returns_article_when_present_in_local_repository() = runTest {
        val article = testArticle(id = "42")
        val repository = FakeArticleLocalRepository(initial = listOf(article))
        val useCase = GetArticleByIdUseCase(repository)

        assertEquals(article, useCase("42"))
    }

    @Test
    fun returns_null_when_article_is_not_found() = runTest {
        val repository = FakeArticleLocalRepository()
        val useCase = GetArticleByIdUseCase(repository)

        assertNull(useCase("missing"))
    }
}
