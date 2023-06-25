package com.gallerydemo.ui.main.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.ui.main.common.EmptyComponent
import com.gallerydemo.ui.main.common.LoadThumbnail
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFolderScreen(
    galleryUiState: GalleryFolderUiState = GalleryFolderUiState(),
    onItemClick: (folder: GalleryFolder) -> Unit
) {
    var isLinearViewStyle by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        GalleryFolderToolbar(isLinearViewStyle) {
            isLinearViewStyle = !isLinearViewStyle
        }
    }, content = { innerPadding ->
        when {
            galleryUiState.isLoading -> {
                ProgressBarLoading()
            }

            galleryUiState.galleryFolderList.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(integerResource(id = if (isLinearViewStyle) R.integer.gallery_folders_linear_span_count else R.integer.gallery_folders_grid_span_count)),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = rememberLazyGridState()
                ) {
                    items(items = galleryUiState.galleryFolderList) { folder ->
                        if (isLinearViewStyle) {
                            ItemLinearGalleryFolderView(folder) {
                                onItemClick.invoke(folder)
                            }
                        } else {
                            ItemGridGalleryFolderView(folder) {
                                onItemClick.invoke(folder)
                            }
                        }
                    }
                }
            }

            else -> {
                EmptyComponent()
            }
        }
    })
}

@Composable
private fun ProgressBarLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}


@Composable
private fun GalleryFolderToolbar(isLinearViewStyle: Boolean, onToggle: () -> Unit) {
    Surface(shadowElevation = dimensionResource(id = R.dimen.toolbar_elevation)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.toolbar_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.folders), style = MaterialTheme.typography.bodyLarge
            )

            Image(painter = painterResource(id = if (isLinearViewStyle) R.drawable.ic_grid_view else R.drawable.ic_linear_view),
                contentDescription = stringResource(
                    R.string.toggle_style
                ),
                modifier = Modifier.clickable {
                    onToggle.invoke()
                })
        }
    }
}

@Composable
private fun ItemLinearGalleryFolderView(folder: GalleryFolder, onItemClick: () -> Unit) {

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick() }
        .padding(horizontal = dimensionResource(id = R.dimen.linear_gallery_folder_parent_container_padding))) {
        val (topMarginSpacer, ivThumbnail, folderName, mediaCount, divider) = createRefs()

        Spacer(modifier = Modifier
            .constrainAs(topMarginSpacer) {
                top.linkTo(parent.top)
            }
            .height(dimensionResource(id = R.dimen.linear_gallery_folder_view_top_spacing)))

        LoadThumbnail(mediaPath = folder.mediaList.firstOrNull()?.mediaPath ?: "",
            isVideo = folder.mediaList.firstOrNull()?.isVideo ?: false, modifier = Modifier
                .constrainAs(ivThumbnail) {
                    start.linkTo(parent.start)
                    top.linkTo(topMarginSpacer.bottom)
                }
                .size(dimensionResource(id = R.dimen.linear_gallery_folder_thumbnail_size))
                .clip(shape = RoundedCornerShape(8.dp)))

        Text(
            text = folder.title ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(folderName) {
                top.linkTo(ivThumbnail.top)
                start.linkTo(ivThumbnail.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(text = folder.mediaList.size.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(mediaCount) {
                top.linkTo(folderName.bottom)
                start.linkTo(folderName.start)
            })

        Divider(modifier = Modifier.constrainAs(divider) {
            top.linkTo(ivThumbnail.bottom, margin = 16.dp)
            start.linkTo(ivThumbnail.start)
            end.linkTo(parent.end)
        })
    }
}


@Composable
private fun ItemGridGalleryFolderView(
    folder: GalleryFolder = GalleryFolder(
        stringResource(id = R.string.all_images), arrayListOf()
    ), onItemClick: () -> Unit
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .clickable { onItemClick() }) {
        val (ivThumbnail, folderName, mediaCount, gradientView) = createRefs()

        LoadThumbnail(
            mediaPath = folder.mediaList.firstOrNull()?.mediaPath ?: "",
            isVideo = folder.mediaList.firstOrNull()?.isVideo ?: false, modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Box(modifier = Modifier
            .constrainAs(gradientView) {
                bottom.linkTo(ivThumbnail.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
            .height(dimensionResource(id = R.dimen.grid_gallery_folder_view_bottom_shadow_layer_height))
            .background(
                brush = Brush.verticalGradient(
                    colors = arrayListOf(
                        Color(0x3744444), Color(0xFF555555)
                    )
                )
            ))

        Text(
            text = folder.title ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.constrainAs(folderName) {
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(ivThumbnail.bottom, margin = 16.dp)
                end.linkTo(mediaCount.start)
                width = Dimension.fillToConstraints
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Text(text = folder.mediaList.size.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.constrainAs(mediaCount) {
                top.linkTo(folderName.top)
                bottom.linkTo(folderName.bottom)
                end.linkTo(parent.end, margin = 16.dp)
            })

    }
}

@Preview
@Composable
private fun GalleryFolderScreenPreview() {
    GalleryDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            GalleryFolderScreen(onItemClick = {})
        }
    }
}