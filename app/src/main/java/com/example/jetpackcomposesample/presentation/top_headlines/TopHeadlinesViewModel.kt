package com.example.jetpackcomposesample.presentation.top_headlines

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.example.jetpackcomposesample.repository.NewsRepository
import com.example.jetpackcomposesample.util.DateConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class TopHeadlinesViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
) : ViewModel() {

    val resource = Pager(
        PagingConfig(
            pageSize = 1,
        )
    ) {
        newsRepository.topHeadlinesSource(Category.GENERAL.name)
    }.flow.map {
        it.map { article ->
            article.copy(publishedAt = DateConverter.toJST(article.publishedAt))
        }
    }
}
