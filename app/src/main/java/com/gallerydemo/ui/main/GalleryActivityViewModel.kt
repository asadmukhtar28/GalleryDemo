package com.gallerydemo.ui.main

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.data.repository.GalleryRepository
import com.gallerydemo.ui.main.folder.GalleryFolderUiState
import com.gallerydemo.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryActivityViewModel @Inject constructor(private val repository: GalleryRepository) :
    ViewModel() {

    private val _galleryUiState = MutableStateFlow(GalleryFolderUiState())
    val galleryUiState = _galleryUiState.asStateFlow()

    private val _selectedItemPosition = MutableStateFlow(GalleryFolder())
    val selectedItemPosition = _selectedItemPosition.asStateFlow()

    private val _isPermissionGrantedFromSettings = MutableStateFlow(false)
    val isPermissionGrantedFromSettings = _isPermissionGrantedFromSettings.asStateFlow()

    private val _selectedMedia = MutableStateFlow(MediaItem())
    val selectedMedia = _selectedMedia.asStateFlow()

    /**
     * As onConfiguration change fetchGallery() again called so before loading the data
     * again, just check if data is loaded then return.
     *
     * isPermissionGrantedFromSettings used for handling the logic that if permission
     * granted from settings then next screen will be opened.
     * */
    fun fetchGallery(
        contentResolver: ContentResolver,
        stringProvider: (resId: Int) -> String,
        isPermissionGrantedFromSettings: Boolean = false
    ) {
        if (galleryUiState.value.galleryFolderList.isNotEmpty()
            || galleryUiState.value.isLoading
        )
            return
        else {
            viewModelScope.launch(Dispatchers.IO) {
                repository.loadMediaFromStorage(
                    contentResolver,
                    stringProvider = { resId -> stringProvider.invoke(resId) })
                    .collectLatest { response ->
                        when (response) {
                            is State.Failure -> {
                                _galleryUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        galleryFolderList = emptyList()
                                    )
                                }
                            }

                            State.Loading -> {
                                _galleryUiState.update { state -> state.copy(isLoading = true) }
                            }

                            is State.Success -> {
                                _galleryUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        galleryFolderList = response.data
                                    )
                                }
                            }
                        }
                    }
            }
        }


        if (isPermissionGrantedFromSettings) {
            _isPermissionGrantedFromSettings.value = true
        }
    }

    fun setSelectedGalleryFolderItem(folder: GalleryFolder) {
        _selectedItemPosition.value = folder
    }

    fun setSelectedMediaItem(mediaItem: MediaItem) {
        _selectedMedia.value = mediaItem
    }
}