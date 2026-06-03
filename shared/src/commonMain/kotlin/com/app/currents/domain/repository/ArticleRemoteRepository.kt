package com.app.currents.domain.repository

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category

interface ArticleRemoteRepository {
    suspend fun getLatest(page: String? = null): Result<List<Article>>
    suspend fun getByCategory(category: Category, page: String? = null): Result<List<Article>>
    suspend fun search(query: String, page: String? = null): Result<List<Article>>
}