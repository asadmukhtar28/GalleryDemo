package com.gallerydemo.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gallerydemo.R
import com.gallerydemo.ui.main.folder.GalleryFolderScreen
import com.gallerydemo.ui.main.media.MediaListScreen
import com.gallerydemo.ui.main.permission.PermissionComponent
import com.gallerydemo.ui.theme.GalleryDemoTheme
import com.gallerydemo.utils.getPermissionList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : ComponentActivity() {
    val viewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            GalleryDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph()
                }
            }
        }
    }

    private fun isNeverAskPermissionSet(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(permission).not()
        } else {
            false
        }

    @Composable
    fun NavigationGraph() {

        var isReadStoragePermission by rememberSaveable {
            mutableStateOf(hasReadStoragePermission())
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { map ->
                val isPermissionGranted = map.values.reduce { acc, value ->
                    acc && value
                }

                if (isPermissionGranted) {
                    isReadStoragePermission = true
                } else {
                    val isNeverAskState = getPermissionList().any { isNeverAskPermissionSet(it) }
                    if (isNeverAskState) {
                        showPermissionSettingsConfirmationDialog()
                    }
                }
            }
        )

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = if (isReadStoragePermission) NavRoutes.FOLDERS_SCREEN else NavRoutes.PERMISSION_SCREEN
        ) {
            composable(NavRoutes.PERMISSION_SCREEN) {
                PermissionComponent { permissionLauncher.launch(getPermissionList()) }
            }
            composable(NavRoutes.FOLDERS_SCREEN) {
                GalleryFolderScreen()
            }
            composable(NavRoutes.MEDIA_SCREEN) {
                MediaListScreen()
            }
        }
    }

    private fun hasReadStoragePermission(): Boolean {
        return getPermissionList().any {
            ActivityCompat.checkSelfPermission(
                this, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openPermissionSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showPermissionSettingsConfirmationDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage("fragment.getString(R.string.message_open_permissions_setting)")
            .setPositiveButton("R.string.settings") { dialog, _ ->
                openPermissionSettings()
                dialog.dismiss()
            }
            .setNegativeButton("R.string.cancel") { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}