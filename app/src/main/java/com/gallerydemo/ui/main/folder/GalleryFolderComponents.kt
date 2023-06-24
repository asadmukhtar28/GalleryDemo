package com.gallerydemo.ui.main.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.gallerydemo.R
import com.gallerydemo.ui.theme.GalleryDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFolderScreen() {
    var isLinearViewStyle by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            GalleryFolderToolbar(isLinearViewStyle) {
                isLinearViewStyle = !isLinearViewStyle
            }
        }) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(integerResource(id = if (isLinearViewStyle) R.integer.gallery_folders_linear_span_count else R.integer.gallery_folders_grid_span_count)),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(if (isLinearViewStyle) 16.dp else 0.dp),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            items(20) {
                if (isLinearViewStyle) {
                    ItemLinearGalleryFolderView()
                } else {
                    ItemGridGalleryFolderView()
                }
            }
        }
    }
}

@Composable
private fun GalleryFolderToolbar(isLinearViewStyle: Boolean, onToggle: () -> Unit) {
    Surface(shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.folders), style = MaterialTheme.typography.bodyLarge
            )

            Image(
                painter = painterResource(id = if (isLinearViewStyle) R.drawable.ic_grid_view else R.drawable.ic_linear_view),
                contentDescription = stringResource(
                    R.string.toggle_style
                ),
                modifier = Modifier.clickable {
                    onToggle.invoke()
                }
            )
        }
    }
}

@Composable
private fun ItemLinearGalleryFolderView() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.linear_gallery_folder_parent_container_padding))
    ) {
        val (ivThumbnail, folderName, mediaCount, divider) = createRefs()

        Image(painter = painterResource(id = R.drawable.ic_default_thumbnail),
            contentDescription = stringResource(R.string.thumbnail),
            modifier = Modifier
                .constrainAs(ivThumbnail) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .size(dimensionResource(id = R.dimen.linear_gallery_folder_thumbnail_size))
                .clip(shape = RoundedCornerShape(8.dp)))

        Text(text = "Folder Name",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.constrainAs(folderName) {
                top.linkTo(ivThumbnail.top)
                start.linkTo(ivThumbnail.end, margin = 16.dp)
            })
        Text(text = "10",
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
private fun ItemGridGalleryFolderView() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (ivThumbnail, folderName, mediaCount) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_default_thumbnail),
            contentDescription = stringResource(R.string.thumbnail),
            modifier = Modifier
                .constrainAs(ivThumbnail) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .aspectRatio(ratio = 1f)
        )

        Text(text = "Folder Name",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.constrainAs(folderName) {
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(ivThumbnail.bottom, margin = 16.dp)
            })

        Text(text = "10",
            style = MaterialTheme.typography.bodySmall,
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
            GalleryFolderScreen()
        }
    }
}