package com.gallerydemo.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.gallerydemo.R


private fun isGranularPermissionsSupport() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun getPermissionList() = if (isGranularPermissionsSupport()) {
    arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
    )
} else {
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}

fun Activity.hasReadStoragePermission(): Boolean {
    return getPermissionList().any {
        ActivityCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.openPermissionSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Activity.showPermissionSettingsConfirmationDialog() {
    val alertDialog: AlertDialog.Builder =
        AlertDialog.Builder(this).setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.message_open_permissions_setting))
            .setPositiveButton(R.string.settings) { dialog, _ ->
                openPermissionSettings()
                dialog.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun Activity.isNeverAskPermissionSet(permission: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission).not()
    } else {
        false
    }
