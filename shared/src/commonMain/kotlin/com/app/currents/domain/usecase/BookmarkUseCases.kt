package com.app.currents.domain.usecase

import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarksUseCase(private val repository: BookmarkRepository) {
    operator fun invoke(): Flow<List<Article>> = repository.getBookmarks()
}

class AddBookmarkUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(article: Article) = repository.addBookmark(article)
}

class RemoveBookmarkUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(articleId: String) = repository.removeBookmark(articleId)
}

class IsBookmarkedUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(articleId: String): Boolean =
        repository.isBookmarked(articleId)
}