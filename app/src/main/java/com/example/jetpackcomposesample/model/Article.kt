package com.example.jetpackcomposesample.model

import kotlinx.serialization.Serializable

/**
 * @see https://newsapi.org/docs/endpoints/top-headlines
 */

@Serializable
data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
)
