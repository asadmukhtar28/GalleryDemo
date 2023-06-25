package com.gallerydemo.data.local.models

data class MediaItem(
    val mediaId: Int,
    val mediaPath: String,
    val mediaWidth: Int,
    val mediaHeight: Int,
    val mediaSize: Long,
    val mimeType: String,
    val isVideo: Boolean = mimeType.startsWith("video", true),
)