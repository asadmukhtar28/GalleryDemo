package com.gallerydemo.ui.main.folder

import com.gallerydemo.data.local.models.GalleryFolder

data class GalleryFolderUiState(
    var isLoading: Boolean = false,
    var galleryFolderList: List<GalleryFolder> = arrayListOf()
)