package com.app.currents.fakes

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleRemoteRepository

class FakeArticleRemoteRepository(
    var latestResult: Result<List<Article>> = Result.success(emptyList()),
    var byCategoryResult: (Category) -> Result<List<Article>> = { Result.success(emptyList()) },
    var searchResult: (String) -> Result<List<Article>> = { Result.success(emptyList()) },
) : ArticleRemoteRepository {

    var getLatestCallCount = 0
        private set
    var getByCategoryCallCount = 0
        private set
    var searchCallCount = 0
        private set

    override suspend fun getLatest(page: String?): Result<List<Article>> {
        getLatestCallCount++
        return latestResult
    }

    override suspend fun getByCategory(category: Category, page: String?): Result<List<Article>> {
        getByCategoryCallCount++
        return byCategoryResult(category)
    }

    override suspend fun search(query: String, page: String?): Result<List<Article>> {
        searchCallCount++
        return searchResult(query)
    }
}
