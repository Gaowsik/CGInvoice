package com.example.cginvoice.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.data.source.remote.model.user.toUserData
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.user.UserData
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.Constants.KEY_WORK_MANAGER_RESPONSE
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.fromJson
import com.example.cginvoice.utills.toJson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    @Assisted private val userRepository: UserRepository,
    @Assisted private val clientRepository: ClientRepository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val syncDataRequestBody = params.inputData.getString(KEY_SYNC_DATA_REQUEST)
        val syncTypeString = params.inputData.getString(KEY_SYNC_TYPE)
        return when (syncTypeString) {
            SyncType.USER.type -> handleUserSync(syncDataRequestBody)

            SyncType.CLIENT.type -> handleClientSync(syncDataRequestBody)
            else -> Result.failure()
        }
    }

    private suspend fun handleUserSync(requestBodyJson: String?): Result {
        val requestBody = requestBodyJson?.fromJson<UserData>()
        return requestBody?.let {
            val response = userRepository.userInfoSync(requestBody)
            manageResponse(response)
        } ?: Result.failure()
    }

    private suspend fun handleClientSync(requestBodyJson: String?): Result {
        val requestBody = requestBodyJson?.fromJson<List<ClientData>>()
        return requestBody?.let {
            val response = clientRepository.syncAllClients(it)
            manageResponse(response)
        } ?: Result.failure()
    }

    private fun manageResponse(response: APIResource<List<IdInfoRemoteResponse>>?): Result {
        return when (response) {
            is APIResource.Success -> {
                Log.d("WorkManager", "Successful!")
                val resultData = workDataOf(KEY_WORK_MANAGER_RESPONSE to response.value.toJson())
                Result.success(resultData)
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

            is APIResource.ErrorString -> TODO()
        }
    }
}