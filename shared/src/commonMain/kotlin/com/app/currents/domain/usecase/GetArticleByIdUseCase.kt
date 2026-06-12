package com.app.currents.domain.usecase

import com.app.currents.domain.model.Article
import com.app.currents.domain.repository.ArticleLocalRepository

class GetArticleByIdUseCase(
    private val repository: ArticleLocalRepository,
) {
    suspend operator fun invoke(id: String): Article? = repository.getArticleById(id)
}