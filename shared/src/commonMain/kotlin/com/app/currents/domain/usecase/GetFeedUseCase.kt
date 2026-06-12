package com.app.currents.domain.usecase


import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.ArticleLocalRepository
import com.app.currents.domain.repository.ArticleRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class GetFeedUseCase(
    private val local: ArticleLocalRepository,
    private val remote: ArticleRemoteRepository,
) {
    operator fun invoke(): Flow<List<Article>> = flow {
        // 1. Emit cache immediately
        val cached = local.getArticles().first()
        if (cached.isNotEmpty()) emit(cached)

        // 2. Fetch fresh data in background
        remote.getLatest().getOrNull()?.let { fresh ->
            local.insertArticles(fresh)
        }

        // 3. Collect ongoing cache updates
        local.getArticles().collect { emit(it) }
    }
}