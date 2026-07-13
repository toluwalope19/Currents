package com.app.currents.fakes

import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeBookmarkRepository(
    initial: List<Article> = emptyList(),
) : BookmarkRepository {

    private val _bookmarks = MutableStateFlow(initial)
    val bookmarksState: StateFlow<List<Article>> = _bookmarks

    override fun getBookmarks() = _bookmarks

    override suspend fun addBookmark(article: Article) {
        _bookmarks.value = _bookmarks.value.filterNot { it.id == article.id } + article
    }

    override suspend fun removeBookmark(articleId: String) {
        _bookmarks.value = _bookmarks.value.filterNot { it.id == articleId }
    }

    override suspend fun isBookmarked(articleId: String): Boolean =
        _bookmarks.value.any { it.id == articleId }
}
