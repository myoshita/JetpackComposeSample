package com.example.jetpackcomposesample.presentation.top_headlines

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_ON
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberImagePainter
import com.example.jetpackcomposesample.model.Article
import com.example.jetpackcomposesample.model.Source
import com.example.jetpackcomposesample.ui.theme.JetpackComposeSampleTheme
import com.example.jetpackcomposesample.ui.theme.Shapes
import com.example.jetpackcomposesample.ui.theme.Typography
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlin.math.roundToInt

enum class Category {
    BUSINESS, ENTERTAINMENT, GENERAL, HEALTH, SCIENCE, SPORTS, TECHNOLOGY
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TopHeadlinesScreen(
    topHeadlinesViewModel: TopHeadlinesViewModel = viewModel()
) {
    val resource = topHeadlinesViewModel.resource.collectAsLazyPagingItems()

    LaunchedEffect(resource.loadState) {
        // TODO error handling
    }

    TopHeadlinesScreen(
        articles = resource,
    )

    if (resource.loadState.append is LoadState.Loading) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun TopHeadlinesScreen(
    articles: LazyPagingItems<Article>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        content = {
            LazyColumn(contentPadding = PaddingValues(top = toolbarHeight)) {
                itemsIndexed(articles) { index, article ->
                    article ?: return@itemsIndexed

                    if (index == 0 && article.urlToImage.isNullOrBlank().not()) {
                        LargeArticle(
                            article = article,
                            onClickArticle = {
                                context.openBrowser(it.url)
                            }
                        )
                    } else {
                        MediumArticle(
                            article = article,
                            onClickArticle = {
                                context.openBrowser(it.url)
                            },
                        )
                    }
                }
            }
            Column {
                TopBar(
                    modifier = Modifier
                        .height(toolbarHeight)
                        .offset {
                            IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt())
                        }
                )
            }
        }
    )
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = "News")
        }
    )
}

@Composable
private fun MediumArticle(
    article: Article,
    onClickArticle: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickArticle(article) }
            .padding(16.dp)
    ) {
        ConstraintLayout {
            val (source, title, publishedAt, image) = createRefs()
            CompositionLocalProvider(LocalContentAlpha provides 0.6f) {
                Text(
                    text = article.source.name,
                    style = Typography.caption,
                    modifier = Modifier.constrainAs(source) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = article.publishedAt,
                    style = Typography.caption,
                    modifier = Modifier.constrainAs(publishedAt) {
                        top.linkTo(title.bottom, 8.dp)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(image.start, 8.dp)
                        width = Dimension.fillToConstraints
                    }
                )
            }
            Text(
                text = article.title,
                style = Typography.body1,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(source.bottom, 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(image.start, 8.dp)
                    width = Dimension.fillToConstraints
                }
            )
            Surface(
                shape = Shapes.medium,
                modifier = Modifier.constrainAs(image) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = article.urlToImage,
                        builder = {
                            placeholder(ColorDrawable(Color.LightGray.toArgb()))
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                )
            }
        }
    }
}

@Composable
private fun LargeArticle(
    article: Article,
    onClickArticle: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickArticle(article) }
            .padding(16.dp)
    ) {
        ConstraintLayout {
            val (title, publishedAt, image, source) = createRefs()
            Surface(
                shape = Shapes.medium,
                modifier = Modifier
                    .constrainAs(image) {
                        width = Dimension.fillToConstraints
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .wrapContentHeight()
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = article.urlToImage,
                        builder = {
                            placeholder(ColorDrawable(Color.LightGray.toArgb()))
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sizeIn(maxHeight = 200.dp)
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides 0.6f) {
                Text(
                    text = article.source.name,
                    style = Typography.caption,
                    modifier = Modifier.constrainAs(source) {
                        top.linkTo(image.bottom, 8.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = article.publishedAt,
                    style = Typography.caption,
                    modifier = Modifier.constrainAs(publishedAt) {
                        top.linkTo(title.bottom, 8.dp)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                )
            }
            Text(
                text = article.title,
                style = Typography.h6,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(source.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

        }
    }
}

private fun Context.openBrowser(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShareState(SHARE_STATE_ON)
        .setShowTitle(true)
        .build()

    customTabsIntent.launchUrl(this, url.toUri())
}

@Preview(showBackground = true)
@Composable
private fun PreviewMediumArticle() {
    val article = Article(
        source = Source(
            id = null,
            name = "Source Name",
        ),
        author = null,
        title = "Title".repeat(20),
        description = null,
        url = "",
        urlToImage = null,
        publishedAt = "publishedAt",
        content = null
    )
    JetpackComposeSampleTheme {
        MediumArticle(article = article, onClickArticle = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLargeArticle() {
    val article = Article(
        source = Source(
            id = null,
            name = "Source Name",
        ),
        author = null,
        title = "Title".repeat(20),
        description = null,
        url = "",
        urlToImage = null,
        publishedAt = "publishedAt",
        content = null
    )
    JetpackComposeSampleTheme {
        LargeArticle(article = article, onClickArticle = {})
    }
}
