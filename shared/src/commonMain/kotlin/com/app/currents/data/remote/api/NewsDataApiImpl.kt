package com.app.currents.data.remote.api

import com.app.currents.data.remote.dto.NewsResponseDto
import com.app.currents.domain.model.Category
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsDataApiImpl(
    private val client: HttpClient,
    private val apiKey: String,
) : NewsDataApi {

    companion object {
        private const val BASE_URL = "https://newsdata.io/api/1"
        private const val LANGUAGE = "en"
    }

    override suspend fun getLatest(page: String?): NewsResponseDto =
        client.get("$BASE_URL/latest") {
            parameter("apikey", apiKey)
            parameter("language", LANGUAGE)
            parameter("image", 1)
            parameter("removeduplicate", 1)
            if (page != null) parameter("page", page)
        }.body()

    override suspend fun getByCategory(category: Category, page: String?): NewsResponseDto =
        client.get("$BASE_URL/latest") {
            parameter("apikey", apiKey)
            parameter("language", LANGUAGE)
            parameter("category", category.apiValue)
            parameter("image", 1)
            parameter("removeduplicate", 1)
            if (page != null) parameter("page", page)
        }.body()

    override suspend fun search(query: String, page: String?): NewsResponseDto =
        client.get("$BASE_URL/latest") {
            parameter("apikey", apiKey)
            parameter("language", LANGUAGE)
            parameter("q", query)
            parameter("image", 1)
            parameter("removeduplicate", 1)
            if (page != null) parameter("page", page)
        }.body()
}