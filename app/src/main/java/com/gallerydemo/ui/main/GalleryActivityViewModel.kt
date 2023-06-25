package com.gallerydemo.ui.main

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.data.repository.GalleryRepository
import com.gallerydemo.ui.main.folder.GalleryFolderUiState
import com.gallerydemo.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryActivityViewModel @Inject constructor(private val repository: GalleryRepository) :
    ViewModel() {

    private val _galleryUiState = MutableStateFlow(GalleryFolderUiState(isLoading = true))
    val galleryUiState: StateFlow<GalleryFolderUiState> = _galleryUiState.asStateFlow()

    private val _selectedItemPosition = MutableStateFlow(GalleryFolder())
    val selectedItemPosition: StateFlow<GalleryFolder> = _selectedItemPosition

    fun fetchGallery(contentResolver: ContentResolver, stringProvider: (resId: Int) -> String) {
        /*
        * As on Configuration change fetchGallery() again called so before loading the data
        * again, just check if data is loaded then return
        */
        if (galleryUiState.value.galleryFolderList.isNotEmpty())
            return
        else
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

    fun setSelectedGalleryFolderItem(folder: GalleryFolder) {
        _selectedItemPosition.value = folder
    }
}