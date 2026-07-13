package com.app.currents.domain.usecase

import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFeedUseCaseTest {

    @Test
    fun emits_remote_result_when_cache_is_empty() = runTest {
        val local = FakeArticleLocalRepository()
        val remote = FakeArticleRemoteRepository(
            latestResult = Result.success(listOf(testArticle(id = "1"))),
        )
        val useCase = GetFeedUseCase(local, remote)

        val emissions = useCase().take(1).toList()

        assertEquals(listOf(testArticle(id = "1")), emissions.first())
    }

    @Test
    fun emits_cache_first_then_merged_result_after_refresh() = runTest {
        val cached = testArticle(id = "cached")
        val fresh = testArticle(id = "fresh")
        val local = FakeArticleLocalRepository(initial = listOf(cached))
        val remote = FakeArticleRemoteRepository(latestResult = Result.success(listOf(fresh)))
        val useCase = GetFeedUseCase(local, remote)

        val emissions = useCase().take(2).toList()

        assertEquals(listOf(cached), emissions[0])
        assertEquals(setOf(cached, fresh), emissions[1].toSet())
    }

    @Test
    fun keeps_cache_untouched_when_remote_fails() = runTest {
        val local = FakeArticleLocalRepository()
        val remote = FakeArticleRemoteRepository(latestResult = Result.failure(RuntimeException("network down")))
        val useCase = GetFeedUseCase(local, remote)

        val emissions = useCase().take(1).toList()

        assertEquals(emptyList(), emissions.first())
    }
}
