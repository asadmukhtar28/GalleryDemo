package com.gallerydemo.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.utils.GalleryMode
import com.gallerydemo.utils.State
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    fun loadMediaFromStorage(
        contentResolver: ContentResolver,
        galleryMode: GalleryMode = GalleryMode.ImageAndVideos,
        stringProvider: (resId: Int) -> String
    ): Flow<State<List<GalleryFolder>>>

    fun getSelectionQuery(galleryMode: GalleryMode): String

    fun getProjectionArray() = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.TITLE,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.SIZE,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    fun fetchMediaUsingContentResolver(
        contentResolver: ContentResolver,
        galleryMode: GalleryMode, stringProvider: (resId: Int) -> String
    ): List<GalleryFolder>?

    fun cursorToGalleryMedia(cursor: Cursor?): Pair<String, MediaItem>?

    fun getUriFromMediaId(mediaId: Long): Uri {
        return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), mediaId.toString())
    }
}