package com.example.cginvoice.presentaion.client

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.SyncDataWorker
import com.example.cginvoice.data.repository.client.ClientRepository
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.user.UserData
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.Constants.KEY_WORK_MANAGER_RESPONSE
import com.example.cginvoice.utills.SyncStatus
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.fromJsonList
import com.example.cginvoice.utills.parseErrors
import com.example.cginvoice.utills.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _getClientInfo = MutableSharedFlow<List<ClientData>>()
    val getClientInfo = _getClientInfo.asSharedFlow()

    private val _currentClient = MutableStateFlow<ClientData?>(ClientData())
    val currentClient = _currentClient.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _clientDetailState = MutableStateFlow(ClientDetailState())
    val clientDetailState = _clientDetailState.asStateFlow()

    private val _isSaved = MutableSharedFlow<Boolean>()
    val isSaved = _isSaved.asSharedFlow()

    private val _workStatus = MutableStateFlow<WorkInfo.State?>(null)
    val workStatus = _workStatus.asStateFlow()

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

    @RequiresApi(Build.VERSION_CODES.O)
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
                    val originalList = response.value
                    _getClientInfo.emit(originalList)
                    val userObjectId = userRepository.getUserObjectId()

                    if (userObjectId.isNullOrBlank()) {
                        _errorMessage.emit("Cannot sync: Missing user ID.")
                        return@launch
                    }

                    val updatedList = originalList.map { it.copy(userInfoObjectId = userObjectId) }

                    startSyncDataWorker(updatedList)
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

    fun updateClientData() {
        viewModelScope.launch {
            setLoading(true)
            updateCurrentUserState()
            val updatedUser = _currentClient.first()
            updatedUser?.let {
                val response = clientRepository.insertOrUpdateClientInfoDB(it)
                when (response) {
                    is DBResource.Error -> {
                        setLoading(false)
                        _errorMessage.emit(response.exception.message.toString())
                    }

                    DBResource.Loading -> TODO()
                    is DBResource.Success -> {
                        setLoading(false)
                        _isSaved.emit(true)
                    }
                }


            } ?: setLoading(false)

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSyncDataWorker(clientDataList: List<ClientData>) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Requires internet
            .build()
        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(
                workDataOf(
                    KEY_SYNC_DATA_REQUEST to clientDataList.toJson(),
                    KEY_SYNC_TYPE to SyncType.CLIENT.type
                )
            )
            .setConstraints(constraints)
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
                    WorkInfo.State.SUCCEEDED -> {
                        _workStatus.value = WorkInfo.State.SUCCEEDED
                        val responseData = it.outputData.getString(KEY_WORK_MANAGER_RESPONSE)
                        responseData?.let { jsonString ->
                            val responseList: List<IdInfoRemoteResponse> = jsonString.fromJsonList()
                            // updateStatusCurrentUser(SyncStatus.COMPLETED.status)
                            // updateObjectIdToCurrentState(responseList)
                            Log.d("SyncDataWorker", "Work succeeded: ${responseList.toString()}")


                        }
                    }

                    WorkInfo.State.FAILED -> viewModelScope.launch { _errorMessage.emit("Work failed: $errorMessage") }
                    else -> {} // Handle other states if needed
                }
            }
        }
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