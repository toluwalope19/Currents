package com.app.currents.domain.usecase


import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleLocalRepository
import com.app.currents.domain.repository.ArticleRemoteRepository

class RefreshFeedUseCase(
    private val local: ArticleLocalRepository,
    private val remote: ArticleRemoteRepository,
) {
    suspend operator fun invoke(category: Category? = null) {
        val articles = if (category == null) {
            remote.getLatest().getOrNull()
        } else {
            remote.getByCategory(category).getOrNull()
        }
        articles?.let {
            local.clearArticles()
            local.insertArticles(it)
        }
    }
}