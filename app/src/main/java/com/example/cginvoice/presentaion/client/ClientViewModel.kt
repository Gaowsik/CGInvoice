package com.example.cginvoice.presentaion.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.utills.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    val clientRepository: ClientRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _getClientInfo = MutableSharedFlow<List<ClientData>>()
    val getClientInfo = _getClientInfo.asSharedFlow()

    private val _currentClient = MutableStateFlow<ClientData?>(null)
    val currentClient = _currentClient.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun getClientInfoRemote(userId: String) {
        viewModelScope.launch {
            setLoading(true)
            val response = clientRepository.getClientRemoteByUserId(userId)
            when (response) {
                is APIResource.Success -> {
                    setLoading(false)
                    _getClientInfo.emit(response.value)
                    response.value.map {
                        clientRepository.insertClientInfoResponseToDB(it.toClientInfoResponse())
                    }

                }

                is APIResource.Loading -> {
                    setLoading(true)
                }

                is APIResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(parseErrors(response))
                }

                else -> {

                }
            }
        }
    }

    fun getClientsDB() {
        viewModelScope.launch {
            setLoading(true)
            val response = clientRepository.getClientsWithContactAndAddress()

            when (response) {
                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())
                }

                DBResource.Loading -> {

                }

                is DBResource.Success -> {
                    setLoading(false)
                    _getClientInfo.emit(response.value)
                    clientRepository.syncAllClients(response.value)
                }

            }
        }
    }

    fun getClientByClientId(clientId: Int) {
        viewModelScope.launch {
            setLoading(true)
            val response = clientRepository.getClientInfo(clientId)
            when (response) {
                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())

                }

                DBResource.Loading -> {
                    TODO()
                }

                is DBResource.Success -> {
                    setLoading(false)
                    setCurrentClient(response.value)
                }
            }

        }
    }

    fun insertClientInfoResponseToDB(clientInfoResponse: ClientInfoResponse) {
        viewModelScope.launch {
            setLoading(true)
            val response = clientRepository.insertClientInfoResponseToDB(clientInfoResponse)
            when (response) {
                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())

                }

                DBResource.Loading -> {
                    TODO()
                }

                is DBResource.Success -> {
                    setLoading(false)
                }
            }

        }
    }

    fun setCurrentClient(client: ClientData) {
        _currentClient.value = client
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }
}