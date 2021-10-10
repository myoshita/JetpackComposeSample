package com.example.jetpackcomposesample.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.jetpackcomposesample.api.NewsApi
import com.example.jetpackcomposesample.model.Article
import com.example.jetpackcomposesample.model.TopHeadlinesResponse.Status.OK
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

interface NewsRepository {
    fun topHeadlinesSource(category: String): PagingSource<Int, Article>
}

private class NewsDataRepository @Inject constructor(
    private val api: NewsApi,
) : NewsRepository {

    companion object {
        private const val INITIAL_PAGE = 1
    }

    override fun topHeadlinesSource(category: String): PagingSource<Int, Article> {
        return object : PagingSource<Int, Article>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
                val page = params.key ?: INITIAL_PAGE
                return runCatching {
                    api.topHeadlines(category = category, page = page)
                }.fold(
                    onSuccess = {
                        if (it.status == OK) {
                            LoadResult.Page(
                                data = it.articles,
                                prevKey = params.key,
                                nextKey = page + 1
                            )
                        } else {
                            LoadResult.Error(Throwable(it.message))
                        }
                    },
                    onFailure = {
                        LoadResult.Error(it)
                    }
                )
            }

            override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
private interface NewsRepositoryBindModule {
    @Binds
    fun bindNewsRepository(
        impl: NewsDataRepository
    ): NewsRepository
}
