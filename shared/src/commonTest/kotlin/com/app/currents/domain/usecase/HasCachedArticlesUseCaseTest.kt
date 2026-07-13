package com.app.currents.domain.usecase

import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HasCachedArticlesUseCaseTest {

    @Test
    fun returns_true_when_local_repository_has_articles() = runTest {
        val repository = FakeArticleLocalRepository(initial = listOf(testArticle()))
        val useCase = HasCachedArticlesUseCase(repository)

        assertTrue(useCase())
    }

    @Test
    fun returns_false_when_local_repository_is_empty() = runTest {
        val repository = FakeArticleLocalRepository()
        val useCase = HasCachedArticlesUseCase(repository)

        assertFalse(useCase())
    }
}
