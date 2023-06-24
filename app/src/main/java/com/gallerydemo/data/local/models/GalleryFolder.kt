package com.gallerydemo.data.local.models

data class GalleryFolder(val title: String, val mediaList: MutableList<MediaItem>) {/*
        override fun equals(other: Any?): Boolean {
            return other is GalleryFolder && other.title == title
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }*/
}