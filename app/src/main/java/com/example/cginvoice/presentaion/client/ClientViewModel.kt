package com.example.cginvoice.presentaion.client

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.utills.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    val clientRepository: ClientRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _getClientInfo = MutableSharedFlow<List<ClientInfoResponse>>()
    val getClientInfo = _getClientInfo.asSharedFlow()


    fun getClientInfo(userId: String) {
        viewModelScope.launch {
            val response = clientRepository.getClientRemoteByUserId(userId)
            when (response) {
                is APIResource.Success -> {
                    _getClientInfo.emit(response.value)
                   // _getClientInfo.insertUserInfoResponseToDB(response.value)
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
    fun syncClientInfo(clientInfoResponse: ClientInfoResponse){
        viewModelScope.launch {
         clientRepository.clientInfoSync(clientInfoResponse)
        }
    }

}