package com.app.currents.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseDto(
    @SerialName("status")       val status: String,
    @SerialName("totalResults") val totalResults: Int = 0,
    @SerialName("results")      val results: List<ArticleDto> = emptyList(),
    @SerialName("nextPage")     val nextPage: String? = null,
)

@Serializable
data class ArticleDto(
    @SerialName("article_id")  val articleId: String,
    @SerialName("title")       val title: String?,
    @SerialName("description") val description: String?,
    @SerialName("content")     val content: String?,
    @SerialName("link")        val link: String?,
    @SerialName("image_url")   val imageUrl: String?,
    @SerialName("source_name") val sourceName: String?,
    @SerialName("creator")     val creator: List<String>? = null,
    @SerialName("category")    val category: List<String>? = null,
    @SerialName("pubDate")     val pubDate: String?,
)