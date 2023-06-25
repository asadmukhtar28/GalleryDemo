package com.gallerydemo.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.gallerydemo.R

fun Activity.hasReadStoragePermission(): Boolean {
    return PermissionHelper.getPermissionList().any {
        ActivityCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.openPermissionSettings() {
    Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        data = uri
    }.run {
        startActivity(this)
    }
}

fun Activity.showPermissionSettingsConfirmationDialog() {
    AlertDialog.Builder(this).apply {
        setTitle(getString(R.string.app_name))
        setMessage(getString(R.string.message_open_permissions_setting))
        setPositiveButton(R.string.settings) { dialog, _ ->
            openPermissionSettings()
            dialog.dismiss()
        }
        setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        setCancelable(false)
    }.create().show()
}

fun Activity.isNeverAskPermissionSet(permission: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        shouldShowRequestPermissionRationale(permission).not()
    } else {
        false
    }
