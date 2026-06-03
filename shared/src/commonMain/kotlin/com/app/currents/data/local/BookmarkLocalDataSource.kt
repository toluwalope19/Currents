package com.app.currents.data.local

import com.app.currents.db.BookmarkEntity
import com.app.currents.db.CurrentsDatabase
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkLocalDataSource(private val database: CurrentsDatabase) {

    private val queries = database.bookmarkQueries

    fun getBookmarks(): Flow<List<Article>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun addBookmark(article: Article) =
        withContext(Dispatchers.Default) {
            queries.insertBookmark(
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
                savedAt     = currentTimeMillis().toString(),
            )
            Unit
        }

    suspend fun removeBookmark(articleId: String) =
        withContext(Dispatchers.Default) {
            queries.deleteById(articleId)
            Unit
        }

    suspend fun isBookmarked(articleId: String): Boolean =
        withContext(Dispatchers.Default) {
            queries.exists(articleId).executeAsOne() > 0
        }

    private fun BookmarkEntity.toDomain(): Article = Article(
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
        isBookmarked = true,
        isBreaking  = isBreaking == 1L,
        isLive      = isLive == 1L,
    )
}