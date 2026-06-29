package com.app.currents.presentation.bookmarks

import com.app.currents.domain.model.Article
import com.app.currents.presentation.base.UiEffect
import com.app.currents.presentation.base.UiEvent
import com.app.currents.presentation.base.UiState

data class BookmarksUiState(
    val bookmarks: List<Article> = emptyList(),
    val filteredBookmarks: List<Article> = emptyList(),
    val selectedFilter: BookmarkFilter = BookmarkFilter.All,
    val isLoading: Boolean = false,
    val sortNewestFirst: Boolean = true,
) : UiState {
    val savedCount: Int get() = bookmarks.size
    val offlineCount: Int get() = bookmarks.size   // all bookmarks available offline
    val unreadCount: Int get() = bookmarks.count { !it.isBookmarked } // placeholder

    // Grouped for section headers
    val grouped: Map<String, List<Article>> get() {
        val sorted = if (sortNewestFirst) filteredBookmarks else filteredBookmarks.reversed()
        // Simple split — recent = saved in last 7 days, older = rest
        // Since we don't store savedAt separately, we split by list position for now
        val recent = sorted.take((sorted.size / 2).coerceAtLeast(1))
        val older = sorted.drop((sorted.size / 2).coerceAtLeast(1))
        return buildMap {
            if (recent.isNotEmpty()) put("RECENT", recent)
            if (older.isNotEmpty()) put("OLDER", older)
        }
    }
}

enum class BookmarkFilter(val label: String) {
    All("All"), Articles("Articles"), Offline("Offline")
}

sealed interface BookmarksUiEvent : UiEvent {
    data class OnFilterSelected(val filter: BookmarkFilter) : BookmarksUiEvent
    data class OnArticleClick(val article: Article) : BookmarksUiEvent
    data class OnRemoveBookmark(val articleId: String) : BookmarksUiEvent
    data object OnToggleSort : BookmarksUiEvent
}

sealed interface BookmarksUiEffect : UiEffect {
    data class NavigateToArticle(val article: Article) : BookmarksUiEffect
}