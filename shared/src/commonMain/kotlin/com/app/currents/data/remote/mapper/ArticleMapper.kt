package com.app.currents.data.remote.mapper


import com.app.currents.data.remote.dto.ArticleDto
import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category

fun ArticleDto.toDomain(): Article = Article(
    id          = articleId,
    title       = title?.trim() ?: "Untitled",
    description = description?.trim() ?: "",
    content     = content?.trim(),
    url         = link ?: "",
    imageUrl    = imageUrl,
    source      = sourceName ?: "Unknown",
    author      = creator?.firstOrNull(),
    category    = category
        ?.firstOrNull()
        ?.let { Category.fromApiValue(it) }
        ?: Category.Top,
    publishedAt = pubDate ?: "",
    isBookmarked = false,
    isBreaking  = false,
    isLive      = false,
)

fun List<ArticleDto>.toDomain(): List<Article> =
    mapNotNull { dto ->
        if (dto.title.isNullOrBlank() && dto.imageUrl == null) null
        else dto.toDomain()
    }