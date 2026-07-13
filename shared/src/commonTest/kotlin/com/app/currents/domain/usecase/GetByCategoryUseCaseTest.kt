package com.app.currents.domain.usecase

import com.app.currents.domain.model.Category
import com.app.currents.fakes.FakeArticleLocalRepository
import com.app.currents.fakes.FakeArticleRemoteRepository
import com.app.currents.fakes.testArticle
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetByCategoryUseCaseTest {

    @Test
    fun emits_only_articles_matching_category_after_refresh() = runTest {
        val local = FakeArticleLocalRepository()
        val remote = FakeArticleRemoteRepository(
            byCategoryResult = { category ->
                Result.success(listOf(testArticle(id = "tech", category = category)))
            },
        )
        val useCase = GetByCategoryUseCase(local, remote)

        val emissions = useCase(Category.Technology).take(1).toList()

        assertEquals(listOf(testArticle(id = "tech", category = Category.Technology)), emissions.first())
    }

    @Test
    fun emits_cached_category_articles_immediately() = runTest {
        val cachedTech = testArticle(id = "cached-tech", category = Category.Technology)
        val cachedSports = testArticle(id = "cached-sports", category = Category.Sports)
        val local = FakeArticleLocalRepository(initial = listOf(cachedTech, cachedSports))
        val remote = FakeArticleRemoteRepository(byCategoryResult = { Result.success(emptyList()) })
        val useCase = GetByCategoryUseCase(local, remote)

        val emissions = useCase(Category.Technology).take(1).toList()

        assertEquals(listOf(cachedTech), emissions.first())
    }

    @Test
    fun requests_the_selected_category_from_remote() = runTest {
        val local = FakeArticleLocalRepository()
        val remote = FakeArticleRemoteRepository()
        val useCase = GetByCategoryUseCase(local, remote)

        useCase(Category.Sports).take(1).toList()

        assertEquals(1, remote.getByCategoryCallCount)
    }
}
