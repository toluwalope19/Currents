package com.app.currents.domain.usecase


import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.ArticleLocalRepository
import com.app.currents.domain.repository.ArticleRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class GetFeedUseCase(
    private val local: ArticleLocalRepository,
    private val remote: ArticleRemoteRepository,
) {
    operator fun invoke(): Flow<List<Article>> =
        local.getArticles().onStart {
            remote.getLatest().getOrNull()?.let { articles ->
                local.insertArticles(articles)
            }
        }
}