package com.app.currents.fakes

import com.app.currents.domain.model.Article
import com.app.currents.domain.model.Category

fun testArticle(
    id: String = "1",
    title: String = "Title $id",
    description: String = "Description $id",
    category: Category = Category.Top,
    source: String = "Source",
    publishedAt: String = "2026-01-01T00:00:00Z",
    url: String = "https://example.com/$id",
) = Article(
    id = id,
    title = title,
    description = description,
    content = null,
    url = url,
    imageUrl = null,
    source = source,
    author = null,
    category = category,
    publishedAt = publishedAt,
)
