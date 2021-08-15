package com.example.jetpackcomposesample.model

import kotlinx.serialization.Serializable

@Serializable
data class TopHeadlinesResponse(
    val status: String,
    val code: String? = null,
    val message: String? = null,
    val totalResults: Int,
    val articles: List<Article>,
)
