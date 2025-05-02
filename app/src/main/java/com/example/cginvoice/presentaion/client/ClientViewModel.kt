package com.example.cginvoice.presentaion.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.utills.SyncStatus
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

    private val _clientDetailState = MutableStateFlow(ClientDetailState())
    val clientDetailState = _clientDetailState.asStateFlow()

    fun updateField(field: (ClientDetailState) -> ClientDetailState) {
        _clientDetailState.value = field(_clientDetailState.value)
    }


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

    fun getClients() {
        viewModelScope.launch {
            setLoading(true)
            val response = clientRepository.getClientList()

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
                    _clientDetailState.value = response.value.toClientDetailState()
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

    private suspend fun updateCurrentUserState(
        userObjectId: String? = "",
        userContactObjectId: String? = "",
        userAddressObjectId: String? = ""
    ) {
        val updatedClientDetail = _clientDetailState.value
        _currentClient.value?.let { currentClient ->
            val updatedClient = currentClient.copy(
                name = updatedClientDetail.name,
                syncStatus = SyncStatus.PENDING.status,
                objectId = if (currentClient.objectId.isNullOrEmpty()) userObjectId else currentClient.objectId,
                address = currentClient.address.copy(
                    city = updatedClientDetail.city,
                    street = updatedClientDetail.street,
                    postalCode = updatedClientDetail.postalCode,
                    aptSuite = updatedClientDetail.suite,
                    country = updatedClientDetail.country,
                    objectId = if (currentClient.address.objectId.isNullOrEmpty()) userAddressObjectId else currentClient.address.objectId
                        ?: userContactObjectId
                ),
                contact = currentClient.contact.copy(
                    name = updatedClientDetail.contactPerson,
                    cell = updatedClientDetail.cell.toLongOrNull() ?: 0L,
                    phone = updatedClientDetail.phone.toLongOrNull()
                        ?: 0L, // Safely convert phone to Long
                    email = updatedClientDetail.email,
                    objectId = if (currentClient.contact.objectId.isNullOrEmpty()) userContactObjectId else currentClient.contact.objectId
                )
            )
            _currentClient.emit(updatedClient)

        }
    }

    fun setCurrentClient(client: ClientData) {
        _currentClient.value = client
    }

    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }
}

data class ClientDetailState(
    val name: String = "",
    val country: String = "",
    val street: String = "",
    val suite: String = "",
    val postalCode: String = "",
    val city: String = "",
    val cell: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = ""
)

fun ClientData.toClientDetailState(): ClientDetailState {
    return ClientDetailState(
        name = name,
        country = address.country,
        street = address.street,
        suite = address.aptSuite,
        postalCode = address.postalCode,
        city = address.city,
        cell = if (contact.cell != 0L) contact.cell.toString() else "",
        contactPerson = contact.name,
        phone = if (contact.phone != 0L) contact.phone.toString() else "",
        email = contact.email
    )
}