package com.gallerydemo.utils

sealed class GalleryMode {
    object Image : GalleryMode()
    object Video : GalleryMode()
    object ImageAndVideos : GalleryMode()
}