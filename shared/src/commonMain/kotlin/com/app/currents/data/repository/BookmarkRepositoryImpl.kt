package com.app.currents.data.repository

import com.app.currents.data.local.BookmarkLocalDataSource
import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class BookmarkRepositoryImpl(
    private val localDataSource: BookmarkLocalDataSource,
) : BookmarkRepository {

    override fun getBookmarks(): Flow<List<Article>> =
        localDataSource.getBookmarks()

    override suspend fun addBookmark(article: Article) =
        localDataSource.addBookmark(article)

    override suspend fun removeBookmark(articleId: String) =
        localDataSource.removeBookmark(articleId)

    override suspend fun isBookmarked(articleId: String): Boolean =
        localDataSource.isBookmarked(articleId)
}