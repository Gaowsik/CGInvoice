package com.example.cginvoice.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepo() {
    suspend fun <T : Any> safeApiCall(
        apiCall: suspend () -> T,
    ): APIResource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall.invoke()
                Log.d("BaseRepo", "safeApiCall: $response")
                APIResource.Success(response)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Log.e("BaseRepo", "safeApiCall: ${throwable.message}")
                        APIResource.Error(
                            false, throwable.code(), throwable.response()?.errorBody()
                        )
                    }

                    else -> {
                        Log.e("BaseRepo", "safeApiCall: ${throwable.message}")
                        APIResource.Error(true, null, null)
                    }
                }
            }
        }

    }

    suspend fun <T : Any> safeDbCall(
        dbCall: suspend () -> T,
    ): DBResource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val result = dbCall.invoke()
                Log.d("BaseRepo", "safeDbCall: $result")
                DBResource.Success(result)
            } catch (exception: Exception) {
                Log.e("BaseRepo", "safeDbCall: ${exception.message}")
                DBResource.Error(exception)
            }
        }
    }

    suspend fun <T : Any> safePrefCall(
        prefCall: suspend () -> T,
    ): DBResource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val result = prefCall.invoke()
                Log.d("BaseRepo", "safePrefCall: ${prefCall.toString()}")
                DBResource.Success(result)
            } catch (exception: Exception) {
                Log.e("BaseRepo", "safePrefCall: ${exception.message}")
                DBResource.Error(exception)
            }
        }
    }

}