package com.gallerydemo.ui.main.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.ui.main.common.EmptyComponent
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListScreen(galleryFolder: GalleryFolder = GalleryFolder()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MediaListToolbar(galleryFolder.title ?: "") }) { innerPadding ->

        when {
            galleryFolder.mediaList.size > 0 -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    columns = GridCells.Fixed(integerResource(id = R.integer.media_grid_span_count))
                ) {
                    items(items = galleryFolder.mediaList) { media ->
                        ItemMediaView(media)
                    }
                }
            }

            else -> EmptyComponent()
        }
    }
}

@Composable
private fun MediaListToolbar(title: String = "All Images") {
    Surface(shadowElevation = dimensionResource(id = R.dimen.toolbar_elevation)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.toolbar_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.toolbar_horizontal_views_padding)),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(
                    R.string.back
                )
            )

            Text(
                text = title, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ItemMediaView(media: MediaItem) {

    Box(modifier = Modifier.fillMaxWidth()) {
        val context = LocalContext.current
        if (media.isVideo) {
            val imageLoader = remember {
                ImageLoader.Builder(context)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .components { add(VideoFrameDecoder.Factory()) }.crossfade(true)
                    .build()
            }
            val painter = rememberAsyncImagePainter(
                model = media.mediaPath,
                imageLoader = imageLoader,
            )

            if (painter.state is AsyncImagePainter.State.Loading) {
                Image(
                    painter = painterResource(id = R.drawable.ic_default_thumbnail),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )
            }

            Image(
                painter = painter,
                contentDescription = stringResource(id = R.string.thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

        } else {
            AsyncImage(
                model = media.mediaPath,
                contentDescription = stringResource(id = R.string.thumbnail),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_default_thumbnail),
                error = painterResource(id = R.drawable.ic_default_thumbnail)
            )
        }

        if (media.isVideo) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.media_play_icon_size))
                    .align(Alignment.Center)
                    .clip(shape = CircleShape)
                    .background(color = Color.Gray)
            ) {
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = stringResource(
                        id = R.string.thumbnail
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaListScreenPreview() {
    GalleryDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            MediaListScreen()
        }
    }
}
