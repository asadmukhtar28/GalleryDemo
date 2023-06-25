package com.gallerydemo.ui.main.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.gallerydemo.R
import com.gallerydemo.ui.theme.GalleryDemoTheme

@Composable
fun PermissionScreen(onAllowButtonClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.permission_block_horizontal_padding))
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.permission_block_content_vertical_spacing))
        ) {
            Text(
                text = stringResource(R.string.gallery_permission),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.gallery_permission_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedButton(
                onClick = { onAllowButtonClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.allow),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionScreenPreview() {
    GalleryDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PermissionScreen {}
        }
    }
}