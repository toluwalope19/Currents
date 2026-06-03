package com.app.currents.domain.model


data class Article(
    val id: String,
    val title: String,
    val description: String,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val author: String?,
    val category: Category,
    val publishedAt: String,
    val isBookmarked: Boolean = false,
    val isBreaking: Boolean = false,
    val isLive: Boolean = false,
)
