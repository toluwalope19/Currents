package com.app.currents.domain.usecase

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleLocalRepository
import com.app.currents.domain.repository.ArticleRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class GetByCategoryUseCase(
    private val local: ArticleLocalRepository,
    private val remote: ArticleRemoteRepository,
) {
    operator fun invoke(category: Category): Flow<List<Article>> = flow {
        // 1. Emit cache immediately
        val cached = local.getArticlesByCategory(category).first()
        if (cached.isNotEmpty()) emit(cached)

        // 2. Fetch fresh
        remote.getByCategory(category).getOrNull()?.let { fresh ->
            local.insertArticles(fresh)
        }

        // 3. Collect ongoing updates
        local.getArticlesByCategory(category).collect { emit(it) }
    }
}