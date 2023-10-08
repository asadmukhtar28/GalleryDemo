package com.gallerydemo.ui.main.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gallerydemo.R
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.ui.main.common.LoadThumbnail
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewScreen(
    media: MediaItem = MediaItem(),
    onBackClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null
) {
    var animateViews by rememberSaveable {
        mutableStateOf(true)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
    }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MediaPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(innerPadding), media
            ) {
                animateViews = !animateViews
            }

            AnimatedVisibility(
                animateViews,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                ToolBar(modifier = Modifier.fillMaxWidth(), onBackClick = {
                    onBackClick?.invoke()
                }, onEditClick = { onEditClick?.invoke() })
            }
        }
    }
}

@Composable
private fun MediaPreview(modifier: Modifier, media: MediaItem, onMediaTap: () -> Unit) {
    Box(
        modifier = modifier.background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        LoadThumbnail(
            mediaPath = media.mediaPath, isVideo = media.isVideo,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMediaTap.invoke() },
            contentScale = ContentScale.FillWidth
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
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = stringResource(
                        id = R.string.thumbnail
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ToolBar(modifier: Modifier, onBackClick: () -> Unit, onEditClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0x80000000),
                        Color(0x00000000)
                    )
                )
            )
            .padding(horizontal = dimensionResource(id = R.dimen.toolbar_horizontal_views_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(painter = painterResource(id = R.drawable.ic_back),
            contentDescription = stringResource(id = R.string.back),
            modifier = Modifier.clickable {
                onBackClick.invoke()
            })

        Image(painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = stringResource(id = R.string.back),
            modifier = Modifier.clickable {
                onEditClick.invoke()
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPreviewScreenPreview() {
    GalleryDemoTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MediaPreviewScreen()
        }
    }
}