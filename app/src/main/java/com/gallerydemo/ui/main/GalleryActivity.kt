package com.gallerydemo.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gallerydemo.ui.main.folder.GalleryFolderScreen
import com.gallerydemo.ui.main.media.MediaListScreen
import com.gallerydemo.ui.main.permission.PermissionComponent
import com.gallerydemo.ui.theme.GalleryDemoTheme
import com.gallerydemo.utils.getPermissionList
import com.gallerydemo.utils.hasReadStoragePermission
import com.gallerydemo.utils.isNeverAskPermissionSet
import com.gallerydemo.utils.showPermissionSettingsConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : ComponentActivity() {
    private val viewModel: GalleryActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (hasReadStoragePermission())
            viewModel.fetchGallery(contentResolver, stringProvider = { resId -> getString(resId) })

        setContent {
            GalleryDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph()
                }
            }
        }
    }

    @Composable
    fun NavigationGraph() {

        var isReadStoragePermission by rememberSaveable {
            mutableStateOf(hasReadStoragePermission())
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                onRequestResponseReceived(permissions) {
                    isReadStoragePermission = true
                    viewModel.fetchGallery(contentResolver, stringProvider = { resId ->
                        getString(resId)
                    })
                }
            }
        )

        val navController = rememberNavController()

        val galleryUiState by viewModel.galleryUiState.collectAsState()
        val selectedGalleryFolder by viewModel.selectedItemPosition.collectAsState()

        NavHost(
            navController = navController,
            startDestination = if (isReadStoragePermission) NavRoutes.FOLDERS_SCREEN else NavRoutes.PERMISSION_SCREEN
        ) {
            composable(NavRoutes.PERMISSION_SCREEN) {
                PermissionComponent { permissionLauncher.launch(getPermissionList()) }
            }

            composable(NavRoutes.FOLDERS_SCREEN) {
                GalleryFolderScreen(
                    galleryUiState = galleryUiState,
                    onItemClick = { folder ->
                        viewModel.setSelectedGalleryFolderItem(folder)
                        navController.navigate(
                            NavRoutes.MEDIA_SCREEN,
                        )
                    })
            }

            composable(NavRoutes.MEDIA_SCREEN) {
                MediaListScreen(selectedGalleryFolder)
            }
        }
    }

    private fun onRequestResponseReceived(
        permissions: Map<String, @JvmSuppressWildcards Boolean>,
        response: () -> Unit
    ) {
        val isPermissionGranted = permissions.values.reduce { acc, value ->
            acc && value
        }

        if (isPermissionGranted) {
            response.invoke()
        } else {
            val isNeverAskState = getPermissionList().any { isNeverAskPermissionSet(it) }
            if (isNeverAskState) {
                showPermissionSettingsConfirmationDialog()
            }
        }
    }

}