package com.app.currents.domain.repository

import com.app.currents.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<Article>>
    suspend fun addBookmark(article: Article)
    suspend fun removeBookmark(articleId: String)
    suspend fun isBookmarked(articleId: String): Boolean
}