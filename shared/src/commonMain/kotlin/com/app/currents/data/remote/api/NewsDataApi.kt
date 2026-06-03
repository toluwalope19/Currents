package com.app.currents.data.remote.api


import com.app.currents.data.remote.dto.NewsResponseDto
import com.app.currents.domain.model.Category

interface NewsDataApi {
    suspend fun getLatest(page: String? = null): NewsResponseDto
    suspend fun getByCategory(category: Category, page: String? = null): NewsResponseDto
    suspend fun search(query: String, page: String? = null): NewsResponseDto
}