package com.app.currents.domain.usecase

import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.ArticleRemoteRepository

class SearchArticlesUseCase(
    private val remote: ArticleRemoteRepository,
) {
    suspend operator fun invoke(query: String): Result<List<Article>> =
        remote.search(query)
}