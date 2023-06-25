package com.gallerydemo.utils

import android.Manifest
import android.os.Build

object PermissionHelper {
    private fun isGranularPermissionsSupport() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun getPermissionList() = if (isGranularPermissionsSupport()) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

}
