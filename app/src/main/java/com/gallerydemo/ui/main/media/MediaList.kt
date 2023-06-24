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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gallerydemo.R
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListScreen() {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { MediaListToolbar() }) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            columns = GridCells.Fixed(integerResource(id = R.integer.media_grid_span_count))
        ) {
            items(20) {
                ItemMediaView()
            }
        }
    }
}

@Composable
private fun MediaListToolbar(title: String = "All Images") {
    Surface(shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(
                    R.string.back
                )
            )

            Text(
                text = title, style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ItemMediaView() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.ic_default_thumbnail),
            contentDescription = stringResource(
                id = R.string.thumbnail
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

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
