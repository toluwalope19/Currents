package com.app.currents.fakes

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import com.app.currents.domain.repository.ArticleLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FakeArticleLocalRepository(
    initial: List<Article> = emptyList(),
) : ArticleLocalRepository {

    private val _articles = MutableStateFlow(initial)
    val articlesState: StateFlow<List<Article>> = _articles

    override fun getArticles() = _articles

    override fun getArticlesByCategory(category: Category) =
        _articles.map { list -> list.filter { it.category == category } }

    override suspend fun getArticleById(id: String): Article? =
        _articles.value.firstOrNull { it.id == id }

    override suspend fun insertArticles(articles: List<Article>) {
        val merged = (_articles.value.filterNot { existing -> articles.any { it.id == existing.id } } + articles)
        _articles.value = merged
    }

    override suspend fun clearArticles() {
        _articles.value = emptyList()
    }

    override suspend fun hasArticles(): Boolean = _articles.value.isNotEmpty()
}
