package com.example.cginvoice.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.fromJson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted private val userRepository: UserRepository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val syncDataRequestBody = params.inputData.getString(KEY_SYNC_DATA_REQUEST)
        val syncTypeString = params.inputData.getString(KEY_SYNC_TYPE)
        return when (syncTypeString?.let { SyncType.valueOf(it) }) {
            SyncType.USER -> handleUserSync(syncDataRequestBody)
            else -> Result.failure()
        }
    }

    private suspend fun handleUserSync(requestBodyJson: String?): Result {
        val requestBody = requestBodyJson?.fromJson<UserInfoResponse>()
        return requestBody?.let {
            val response = userRepository.userInfoSync(requestBody)
            manageResponse(response)
        } ?: Result.failure()
    }

    private fun manageResponse(response: APIResource<Unit>?): Result {
        return when (response) {
            is APIResource.Success -> {
                Log.d("WorkManager", "Successful!")
                Result.success()
            }

            is APIResource.Error -> {
                Log.d("WorkManager", "Error")
                Result.failure()
            }

            is APIResource.Loading -> {
                Result.retry()
            }

            null -> {
                Result.failure()
            }
        }
    }

}