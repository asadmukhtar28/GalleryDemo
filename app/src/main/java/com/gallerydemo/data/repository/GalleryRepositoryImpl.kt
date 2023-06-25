package com.gallerydemo.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.utils.GalleryMode
import com.gallerydemo.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor() : GalleryRepository {

    override fun loadMediaFromStorage(
        contentResolver: ContentResolver,
        galleryMode: GalleryMode,
        stringProvider: (resId: Int) -> String
    ): Flow<State<List<GalleryFolder>>> {
        return flow {
            emit(State.Loading)
            val data =
                fetchMediaUsingContentResolver(contentResolver, galleryMode, stringProvider)
            if (data != null) emit(State.Success(data))
            else emit(State.Failure())
        }.flowOn(Dispatchers.IO)
    }

    override fun getSelectionQuery(galleryMode: GalleryMode): String {
        return when (galleryMode) {
            GalleryMode.ImageAndVideos -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append(" IN(").append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE).append(",")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO).append(")").toString()

            GalleryMode.Video -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=").append(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO).toString()

            GalleryMode.Image -> StringBuilder().append(MediaStore.Files.FileColumns.MEDIA_TYPE)
                .append("=").append(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE).toString()
        }
    }

    override fun fetchMediaUsingContentResolver(
        contentResolver: ContentResolver,
        galleryMode: GalleryMode,
        stringProvider: (resId: Int) -> String
    ): List<GalleryFolder>? {
        return kotlin.runCatching {
            val folders = mutableListOf<GalleryFolder>()
            val cursor: Cursor?
            val uri: Uri = MediaStore.Files.getContentUri("external")
            val selection: String = getSelectionQuery(galleryMode)
            val projection = getProjectionArray()
            val orderBy = MediaStore.Images.Media.DATE_TAKEN
            cursor = contentResolver.query(
                uri, projection, selection, null, "$orderBy DESC"
            )
            if (cursor != null) {
                val foldersMap =
                    mutableMapOf<String, GalleryFolder>() // key is folderName here we can group media by folder names
                val allImageMedia: MutableList<MediaItem> by lazy { mutableListOf() }
                val allVideoMedia: MutableList<MediaItem> by lazy { mutableListOf() }
                val canAddImages =
                    galleryMode != GalleryMode.Video // if not restricted to have videos only
                val canAddVideos =
                    galleryMode != GalleryMode.Image // if not restricted to have images only
                while (cursor.moveToNext()) {
                    cursorToGalleryMedia(cursor)?.let { (folderName, mediaItem) ->

                        if (canAddVideos && mediaItem.isVideo) allVideoMedia.add(mediaItem)
                        else if (canAddImages && !mediaItem.isVideo) allImageMedia.add(mediaItem)

                        /* if has folder model already added then append media to that, else create
                        a new folder and add media into that */

                        foldersMap.getOrPut(folderName) {
                            GalleryFolder(folderName, mutableListOf())
                        }.mediaList.add(mediaItem)

                    }
                }
                if (canAddImages) folders.add(
                    GalleryFolder(
                        stringProvider.invoke(R.string.all_images), allImageMedia
                    )
                )
                if (canAddVideos) folders.add(
                    GalleryFolder(
                        stringProvider.invoke(R.string.all_videos), allVideoMedia
                    )
                )
                folders.addAll(foldersMap.values)
            }
            folders
        }.getOrNull()
    }

    override fun cursorToGalleryMedia(cursor: Cursor?): Pair<String, MediaItem>? {
        return cursor?.let {
            kotlin.runCatching {
                Pair(
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    MediaItem(
                        mediaId = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                        mediaWidth = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)),
                        mediaHeight = it.getInt(it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)),
                        mediaPath = getUriFromMediaId(
                            cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Images.Media._ID
                                )
                            )
                        ).toString(),
                        mediaSize = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                        mimeType = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                    )
                )
            }.getOrNull()
        }
    }

    override fun getUriFromMediaId(mediaId: Long): Uri {
        return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), mediaId.toString())
    }
}