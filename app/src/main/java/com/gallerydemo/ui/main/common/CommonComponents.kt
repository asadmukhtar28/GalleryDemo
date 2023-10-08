package com.gallerydemo.ui.main.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import com.gallerydemo.R


@Composable
fun EmptyComponent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_gallery),
                contentDescription = stringResource(
                    id = R.string.thumbnail
                ),
                modifier = Modifier.size(dimensionResource(id = R.dimen.empty_gallery_icon_size))
            )

            Text(
                text = stringResource(id = R.string.empty_media_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun LoadThumbnail(
    mediaPath: String, isVideo: Boolean, modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    if (isVideo) {
        val imageLoader = remember {
            ImageLoader.Builder(context).memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED).respectCacheHeaders(true)
                .components { add(VideoFrameDecoder.Factory()) }.crossfade(true).build()
        }
        val painter = rememberAsyncImagePainter(
            model = mediaPath,
            imageLoader = imageLoader,
        )

        if (painter.state is AsyncImagePainter.State.Loading) {
            Image(
                painter = painterResource(id = R.drawable.ic_default_thumbnail),
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale,
            )
        }

        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.thumbnail),
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = mediaPath,
            contentDescription = stringResource(id = R.string.thumbnail),
            modifier = modifier,
            contentScale = contentScale,
            placeholder = painterResource(id = R.drawable.ic_default_thumbnail),
            error = painterResource(id = R.drawable.ic_default_thumbnail)
        )
    }
}