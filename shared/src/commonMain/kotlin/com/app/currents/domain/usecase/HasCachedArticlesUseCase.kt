package com.app.currents.domain.usecase

import com.app.currents.domain.repository.ArticleLocalRepository

class HasCachedArticlesUseCase(
    private val repository: ArticleLocalRepository,
) {
    suspend operator fun invoke(): Boolean = repository.hasArticles()
}