package com.app.currents.domain.repository


import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ArticleLocalRepository {
    fun getArticles(): Flow<List<Article>>
    fun getArticlesByCategory(category: Category): Flow<List<Article>>
    suspend fun getArticleById(id: String): Article?
    suspend fun insertArticles(articles: List<Article>)
    suspend fun clearArticles()
}