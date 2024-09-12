package com.example.cginvoice.presentaion.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.SyncDataWorker
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.parseErrors
import com.example.cginvoice.utills.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _getUserInfo = MutableSharedFlow<UserInfoResponse>()
    val getUserInfo = _getUserInfo.asSharedFlow()

    private val _workStatus = MutableStateFlow<WorkInfo.State?>(null)
    val workStatus = _workStatus.asStateFlow()

    /*    fun insertUserRemote(user: User, contact: Contact, address: Address) {
            viewModelScope.launch {
                userRepository.insertUserRemote(user, contact, address)
            }

        }*/

    fun getUserInfo(objectId: String) {
        viewModelScope.launch {
            val response = userRepository.getUserInfoRemote(objectId)
            when (response) {
                is APIResource.Success -> {
                    _getUserInfo.emit(response.value)
                    userRepository.insertUserInfoResponseToDB(response.value)
                }

                is APIResource.Loading -> {

                }

                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(response))
                }

                else -> {

                }

            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateOrSaveUserInfo(userInfoResponse: UserInfoResponse){
        viewModelScope.launch {
           val response = userRepository.insertUserInfoResponseToDB(userInfoResponse)
            if (response is DBResource.Success){
                val responseDB = userRepository.getUserInfo()
                if (responseDB is DBResource.Success){
                    startIncomingSyncDataWorker(responseDB.value)
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startIncomingSyncDataWorker(userInfoResponse: UserInfoResponse) {
        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(workDataOf(KEY_SYNC_DATA_REQUEST to userInfoResponse.toJson(),KEY_SYNC_TYPE to SyncType.USER.type))
            .setInitialDelay(Duration.ofSeconds(20))
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10))
            .build()

        workManager.enqueue(workRequest)
        observeWorkStatus(workRequest.id)
    }

    private fun observeWorkStatus(workId: UUID) {
        workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
            workInfo?.let {
                _workStatus.value = it.state
                when (it.state) {
                    WorkInfo.State.SUCCEEDED ->  _workStatus.value = WorkInfo.State.SUCCEEDED
                    WorkInfo.State.FAILED -> viewModelScope.launch {  _errorMessage.emit("Work failed: $errorMessage")}
                    else -> {} // Handle other states if needed
                }
            }
        }
    }


    /*    fun updateUserInfo(userInfoResponse: UserInfoResponse){
            viewModelScope.launch {
                remoteUserDataSource.updateUserRemote(userInfoResponse)
            }
        }*/


}