package com.example.jetpackcomposesample.api

import com.example.jetpackcomposesample.model.TopHeadlinesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun topHeadlines(
        @Query("country") country: String = "jp",
        @Query("category") category: String = "GENERAL",
        @Query("pageSize") pageSize: Int = 100,
        @Query("page") page: Int = 1
    ): TopHeadlinesResponse
}
