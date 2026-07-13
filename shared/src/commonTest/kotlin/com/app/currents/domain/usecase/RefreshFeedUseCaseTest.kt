package com.app.currents.domain.usecase

import com.app.currents.domain.model.Category
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RefreshFeedUseCaseTest {

    @Test
    fun replaces_cache_with_latest_when_no_category_given() = runTest {
        val local = FakeArticleLocalRepository(initial = listOf(testArticle(id = "stale")))
        val fresh = listOf(testArticle(id = "fresh"))
        val remote = FakeArticleRemoteRepository(latestResult = Result.success(fresh))
        val useCase = RefreshFeedUseCase(local, remote)

        useCase(category = null)

        assertEquals(fresh, local.articlesState.value)
    }

    @Test
    fun replaces_cache_with_category_result_when_category_given() = runTest {
        val local = FakeArticleLocalRepository(initial = listOf(testArticle(id = "stale")))
        val fresh = listOf(testArticle(id = "fresh-tech", category = Category.Technology))
        val remote = FakeArticleRemoteRepository(byCategoryResult = { Result.success(fresh) })
        val useCase = RefreshFeedUseCase(local, remote)

        useCase(category = Category.Technology)

        assertEquals(fresh, local.articlesState.value)
        assertEquals(1, remote.getByCategoryCallCount)
    }

    @Test
    fun leaves_cache_untouched_when_remote_fails() = runTest {
        val stale = listOf(testArticle(id = "stale"))
        val local = FakeArticleLocalRepository(initial = stale)
        val remote = FakeArticleRemoteRepository(latestResult = Result.failure(RuntimeException("boom")))
        val useCase = RefreshFeedUseCase(local, remote)

        useCase(category = null)

        assertEquals(stale, local.articlesState.value)
    }
}
