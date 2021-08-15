package com.example.jetpackcomposesample.api

import android.content.Context
import com.example.jetpackcomposesample.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ApiProvideModule {

    @Provides
    @Singleton
    fun provideClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("X-Api-Key", BuildConfig.NEWS_API_KEY)
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Provides
    @Singleton
    @OptIn(ExperimentalSerializationApi::class)
    fun provideNewsApi(okHttpClient: OkHttpClient): NewsApi {
        val contentType = "application/json".toMediaType()
        val format = Json {
            coerceInputValues = true
        }
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(format.asConverterFactory(contentType))
            .build()
        return retrofit.create(NewsApi::class.java)
    }
}