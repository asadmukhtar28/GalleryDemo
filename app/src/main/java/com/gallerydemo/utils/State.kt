package com.gallerydemo.utils

sealed class State<out T> {
    data class Success<T>(val data: T) : State<T>()
    data class Failure(val error: String? = null) : State<Nothing>()
    object Loading : State<Nothing>()
}