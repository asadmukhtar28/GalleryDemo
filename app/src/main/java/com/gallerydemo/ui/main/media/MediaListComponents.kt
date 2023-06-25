package com.gallerydemo.ui.main.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.ui.main.common.EmptyComponent
import com.gallerydemo.ui.main.common.LoadThumbnail
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListScreen(
    galleryFolder: GalleryFolder = GalleryFolder(),
    onBackIconClick: (() -> Unit)? = null
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MediaListToolbar(galleryFolder.title ?: "") {
                onBackIconClick?.invoke()
            }
        }) { innerPadding ->

        when {
            galleryFolder.mediaList.size > 0 -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    columns = GridCells.Fixed(integerResource(id = R.integer.media_grid_span_count)),
                    state = rememberLazyGridState()
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
private fun MediaListToolbar(
    title: String = stringResource(id = R.string.all_images),
    onBackIconClick: () -> Unit
) {
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
                ), modifier = Modifier.clickable { onBackIconClick.invoke() }
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
        LoadThumbnail(
            mediaPath = media.mediaPath,
            isVideo = media.isVideo, modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

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
