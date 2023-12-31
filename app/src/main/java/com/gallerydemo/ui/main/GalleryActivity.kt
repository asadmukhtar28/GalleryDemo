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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gallerydemo.ui.main.folder.GalleryFolderScreen
import com.gallerydemo.ui.main.media.MediaListScreen
import com.gallerydemo.ui.main.permission.PermissionScreen
import com.gallerydemo.ui.main.preview.MediaPreviewScreen
import com.gallerydemo.ui.theme.GalleryDemoTheme
import com.gallerydemo.utils.PermissionHelper
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
        checkPermissionAndFetchGallery()
        setContent {
            GalleryDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    InitUi(hasReadStoragePermission())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionAndFetchGallery(true)
    }

    private fun checkPermissionAndFetchGallery(isFromOnResume: Boolean = false) {
        if (hasReadStoragePermission()) viewModel.fetchGallery(
            contentResolver = contentResolver,
            stringProvider = { resId -> getString(resId) },
            isPermissionGrantedFromSettings = isFromOnResume
        )
    }

    @Composable
    fun InitUi(isReadStoragePermissionGranted: Boolean) {
        val galleryUiState by viewModel.galleryUiState.collectAsStateWithLifecycle()
        val selectedGalleryFolder by viewModel.selectedItemPosition.collectAsStateWithLifecycle()
        val isPermissionGrantedFromSettings by viewModel.isPermissionGrantedFromSettings.collectAsStateWithLifecycle()
        val selectedMedia by viewModel.selectedMedia.collectAsStateWithLifecycle()
        val navController = rememberNavController()

        val permissionLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    onRequestResponseReceived(permissions) {
                        navController.navigate(
                            NavRoutes.FOLDERS_SCREEN,
                        ) {
                            popUpTo(NavRoutes.PERMISSION_SCREEN) {
                                inclusive = true
                            }
                        }
                        viewModel.fetchGallery(contentResolver, stringProvider = { resId ->
                            getString(resId)
                        })
                    }
                })

        NavHost(
            navController = navController,
            startDestination = if (isReadStoragePermissionGranted) NavRoutes.FOLDERS_SCREEN else NavRoutes.PERMISSION_SCREEN
        ) {
            composable(NavRoutes.PERMISSION_SCREEN) {
                PermissionScreen {
                    permissionLauncher.launch(PermissionHelper.getPermissionList())
                }
            }

            composable(NavRoutes.FOLDERS_SCREEN) {
                GalleryFolderScreen(galleryUiState = galleryUiState, onItemClick = { folder ->
                    navController.navigate(
                        NavRoutes.MEDIA_SCREEN,
                    )
                    viewModel.setSelectedGalleryFolderItem(folder)
                })
            }

            composable(NavRoutes.MEDIA_SCREEN) {
                MediaListScreen(selectedGalleryFolder, onBackIconClick = {
                    navController.popBackStack()
                }) { mediaItem ->
                    viewModel.setSelectedMediaItem(mediaItem)
                    navController.navigate(NavRoutes.MEDIA_PREVIEW_SCREEN)
                }
            }
            composable(NavRoutes.MEDIA_PREVIEW_SCREEN) {
                MediaPreviewScreen(
                    selectedMedia,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {})
            }
        }

        if (isPermissionGrantedFromSettings) {
            navController.navigate(
                NavRoutes.FOLDERS_SCREEN,
            ) {
                popUpTo(NavRoutes.PERMISSION_SCREEN) {
                    inclusive = true
                }
            }
        }
    }

    private fun onRequestResponseReceived(
        permissions: Map<String, @JvmSuppressWildcards Boolean>, response: () -> Unit
    ) {
        val isPermissionGranted = permissions.values.reduce { acc, value ->
            acc && value
        }

        if (isPermissionGranted) {
            response.invoke()
        } else {
            val isNeverAskState =
                PermissionHelper.getPermissionList().any { isNeverAskPermissionSet(it) }
            if (isNeverAskState) {
                showPermissionSettingsConfirmationDialog()
            }
        }
    }

}