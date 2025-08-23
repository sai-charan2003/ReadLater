package com.charan.readlater.utils

sealed class ProcessState<out T> {
    data class Success<out T>(val data: T) : ProcessState<T>()
    data class Error(val exception: String) : ProcessState<Nothing>()
    object Loading : ProcessState<Nothing>()
    object NotDetermined : ProcessState<Nothing>()

}