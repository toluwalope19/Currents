package com.app.currents.data.local

import com.app.currents.db.ArticleEntity
import com.app.currents.db.CurrentsDatabase
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleLocalDataSource(private val database: CurrentsDatabase) {

    private val queries = database.articleQueries

    fun getArticles(): Flow<List<Article>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    fun getArticlesByCategory(category: Category): Flow<List<Article>> =
        queries.selectByCategory(category.apiValue)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun getArticleById(id: String): Article? =
        withContext(Dispatchers.Default) {
            queries.selectById(id).executeAsOneOrNull()?.toDomain()
        }

    suspend fun insertArticles(articles: List<Article>) =
        withContext(Dispatchers.Default) {
            database.transaction {
                articles.forEach { article ->
                    queries.insertArticle(
                        id          = article.id,
                        title       = article.title,
                        description = article.description,
                        content     = article.content,
                        url         = article.url,
                        imageUrl    = article.imageUrl,
                        source      = article.source,
                        author      = article.author,
                        category    = article.category.apiValue,
                        publishedAt = article.publishedAt,
                        isBreaking  = if (article.isBreaking) 1L else 0L,
                        isLive      = if (article.isLive) 1L else 0L,
                        fetchedAt   = currentTimeMillis(),
                    )
                }
            }
        }

    suspend fun clearArticles() =
        withContext(Dispatchers.Default) {
            queries.deleteAll()
            Unit
        }

    suspend fun hasArticles(): Boolean =
        withContext(Dispatchers.Default) {
            queries.selectAll().executeAsList().isNotEmpty()
        }

    suspend fun deleteStale(olderThanMillis: Long) =
        withContext(Dispatchers.Default) {
            queries.deleteStale(olderThanMillis)
        }

    private fun ArticleEntity.toDomain(): Article = Article(
        id          = id,
        title       = title,
        description = description,
        content     = content,
        url         = url,
        imageUrl    = imageUrl,
        source      = source,
        author      = author,
        category    = Category.fromApiValue(category),
        publishedAt = publishedAt,
        isBookmarked = false,
        isBreaking  = isBreaking == 1L,
        isLive      = isLive == 1L,
    )
}