package com.app.currents.domain.usecase

import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchArticlesUseCaseTest {

    @Test
    fun returns_remote_search_results_on_success() = runTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { query -> Result.success(listOf(testArticle(id = query))) },
        )
        val useCase = SearchArticlesUseCase(remote)

        val result = useCase("kotlin")

        assertEquals(listOf(testArticle(id = "kotlin")), result.getOrNull())
        assertEquals(1, remote.searchCallCount)
    }

    @Test
    fun propagates_failure_from_remote() = runTest {
        val remote = FakeArticleRemoteRepository(
            searchResult = { Result.failure(RuntimeException("search failed")) },
        )
        val useCase = SearchArticlesUseCase(remote)

        val result = useCase("kotlin")

        assertTrue(result.isFailure)
    }
}
