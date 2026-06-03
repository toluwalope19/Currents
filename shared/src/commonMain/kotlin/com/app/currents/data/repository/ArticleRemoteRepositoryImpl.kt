package com.app.currents.data.repository


import com.app.currents.data.remote.api.NewsDataApi
import com.app.currents.data.remote.mapper.toDomain
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleRemoteRepository

class ArticleRemoteRepositoryImpl(
    private val api: NewsDataApi,
) : ArticleRemoteRepository {

    override suspend fun getLatest(page: String?): Result<List<Article>> =
        runCatching {
            api.getLatest(page).results.toDomain()
        }

    override suspend fun getByCategory(category: Category, page: String?): Result<List<Article>> =
        runCatching {
            api.getByCategory(category, page).results.toDomain()
        }

    override suspend fun search(query: String, page: String?): Result<List<Article>> =
        runCatching {
            api.search(query, page).results.toDomain()
        }
}