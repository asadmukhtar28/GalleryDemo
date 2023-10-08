package com.gallerydemo.data.local.models

data class MediaItem(
    val mediaId: Int = -1,
    val mediaPath: String = "",
    val mediaWidth: Int = -1,
    val mediaHeight: Int = -1,
    val mediaSize: Long = 0,
    val mimeType: String = "",
    val isVideo: Boolean = mimeType.startsWith("video", true),
)