package com.example.cginvoice.data

import okhttp3.ResponseBody

sealed class APIResource<out T> {
    data class Success<out T>(val value: T) : APIResource<T>()
    data class Error(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : APIResource<Nothing>()

    data class ErrorString(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: String?
    ) : APIResource<Nothing>()

    object Loading : APIResource<Nothing>()
}