package com.app.currents.data.repository

import com.app.currents.data.local.ArticleLocalDataSource
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleLocalRepository
import kotlinx.coroutines.flow.Flow

class ArticleLocalRepositoryImpl(
    private val localDataSource: ArticleLocalDataSource,
) : ArticleLocalRepository {

    override fun getArticles(): Flow<List<Article>> =
        localDataSource.getArticles()

    override fun getArticlesByCategory(category: Category): Flow<List<Article>> =
        localDataSource.getArticlesByCategory(category)

    override suspend fun getArticleById(id: String): Article? =
        localDataSource.getArticleById(id)

    override suspend fun insertArticles(articles: List<Article>) =
        localDataSource.insertArticles(articles)

    override suspend fun clearArticles() =
        localDataSource.clearArticles()

    override suspend fun hasArticles(): Boolean =
        localDataSource.hasArticles()
}