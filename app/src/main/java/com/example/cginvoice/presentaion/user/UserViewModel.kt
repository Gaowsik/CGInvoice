package com.example.cginvoice.presentaion.user

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.UserData
import com.example.cginvoice.domain.model.user.toUserDetailState
import com.example.cginvoice.utills.Constants.KEY_SYNC_DATA_REQUEST
import com.example.cginvoice.utills.Constants.KEY_SYNC_TYPE
import com.example.cginvoice.utills.Constants.KEY_WORK_MANAGER_RESPONSE
import com.example.cginvoice.utills.SyncStatus
import com.example.cginvoice.utills.SyncType
import com.example.cginvoice.utills.fromJsonList
import com.example.cginvoice.utills.parseErrors
import com.example.cginvoice.utills.toJson
import com.parse.ParseFile
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

    private val _currentUserState = MutableStateFlow<UserData?>(UserData())
    val currentUserState = _currentUserState.asStateFlow()

    private val _userDetailState = MutableStateFlow(UserDetailState())
    val userDetailState = _userDetailState.asStateFlow()

    private val _isSync = MutableSharedFlow<Boolean>()
    val isSync = _isSync.asSharedFlow()

    private val _isSaved = MutableSharedFlow<Boolean>()
    val isSaved = _isSaved.asSharedFlow()

    private val _isUploaded = MutableSharedFlow<Boolean>()
    val isUploaded = _isUploaded.asSharedFlow()

    fun updateField(field: (UserDetailState) -> UserDetailState) {
        _userDetailState.value = field(_userDetailState.value)
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateCurrentUserField(update: (UserData) -> UserData) {
        _currentUserState.value?.let { currentUser ->
            val updatedUser = update(currentUser)
            _currentUserState.value = updatedUser
        }
    }


    /*    fun insertUserRemote(user: User, contact: Contact, address: Address) {
            viewModelScope.launch {
                userRepository.insertUserRemote(user, contact, address)
            }

        }*/

    @SuppressLint("NewApi")
    fun getUserInfo() {
        viewModelScope.launch {
            setLoading(true)
            val response = userRepository.getUserInfo()
            when (response) {
                is DBResource.Success -> {
                    setLoading(false)
                    _userDetailState.emit(response.value.toUserDetailState())
                    _currentUserState.emit(response.value)
                    if (response.value.syncStatus == SyncStatus.PENDING.status) {
                        startIncomingSyncDataWorker(response.value)
                    }

                    //  userRepository.insertUserInfoResponseToDB(response.value.toUserInfoResponse())
                }

                is DBResource.Loading -> {
                    setLoading(false)
                }

                is DBResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(response.exception.message.toString())

                }

                else -> {

                }

            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateOrSaveUserInfo(userInfoResponse: UserInfoResponse) {
        viewModelScope.launch {
            val response = userRepository.insertUserInfoResponseToDB(userInfoResponse)
            if (response is DBResource.Success) {
                /*       val responseDB = userRepository.getUserInfo()
                       if (responseDB is DBResource.Success) {
                           //   startIncomingSyncDataWorker(responseDB.value)
                       }*/

            }
        }
    }


    fun updateUserDataDB() {
        viewModelScope.launch {
            setLoading(true)
            updateCurrentUserState()
            val updatedUser = _currentUserState.first()
            updatedUser?.let {
                val response = userRepository.insertOrUpdateUserInfoDB(it)
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
            }


        }
    }

    fun updateStatusCurrentUser(status: String) {
        viewModelScope.launch {
            setLoading(true)
            val currentUserId = _currentUserState.first()?.userId
            currentUserId?.let {
                val response = userRepository.updateStatusByUserID(it, status)
                when (response) {
                    is DBResource.Error -> {
                        setLoading(false)
                        _errorMessage.emit(response.exception.message.toString())
                    }

                    DBResource.Loading -> TODO()
                    is DBResource.Success -> {
                        setLoading(false)
                    }
                }
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUserData() {
        viewModelScope.launch {
            setLoading(true)
            updateCurrentUserState()
            val updatedUser = _currentUserState.first()
            updatedUser?.let {
                val response = userRepository.userInfoSync(it)

                when (response) {
                    is APIResource.Success -> {
                        setLoading(false)
                        _isSync.emit(true)
                    }

                    is APIResource.Loading -> {
                        setLoading(true)
                    }

                    is APIResource.Error -> {
                        setLoading(false)
                        _errorMessage.emit(response.errorBody.toString())
                    }

                    is APIResource.ErrorString -> {
                        setLoading(false)
                        _errorMessage.emit(response.errorBody.toString())
                    }
                } /*else {
                userRepository.userInfoSync(_userDetailState.value.toUserData())
            }*/
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startIncomingSyncDataWorker(userData: UserData) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Requires internet
            .build()
        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(
                workDataOf(
                    KEY_SYNC_DATA_REQUEST to userData.toJson(),
                    KEY_SYNC_TYPE to SyncType.USER.type
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
                            updateStatusCurrentUser(SyncStatus.COMPLETED.status)
                            updateObjectIdToCurrentState(responseList)
                            Log.d("SyncDataWorker", "Work succeeded: ${responseList.toString()}")


                        }
                    }

                    WorkInfo.State.FAILED -> viewModelScope.launch { _errorMessage.emit("Work failed: $errorMessage") }
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
    private suspend fun updateCurrentUserState(
        userObjectId: String? = "",
        userContactObjectId: String? = "",
        userAddressObjectId: String? = ""
    ) {
        val userDetail = _userDetailState.value
        _currentUserState.value?.let { currentUser ->
            val updatedUser = currentUser.copy(
                businessName = userDetail.name,
                syncStatus = SyncStatus.PENDING.status,
                logo = userDetail.logo,
                signature = userDetail.signature,
                objectId = if (currentUser.objectId.isNullOrEmpty()) userObjectId else currentUser.objectId,
                address = currentUser.address.copy(
                    city = userDetail.city,
                    street = userDetail.street,
                    postalCode = userDetail.postalCode,
                    aptSuite = userDetail.suite,
                    country = userDetail.country,
                    objectId = if (currentUser.address.objectId.isNullOrEmpty()) userAddressObjectId else currentUser.address.objectId
                        ?: userContactObjectId
                ),
                contact = currentUser.contact.copy(
                    name = userDetail.contactPerson,
                    cell = userDetail.cell.toLongOrNull() ?: 0L,
                    phone = userDetail.phone.toLongOrNull()
                        ?: 0L, // Safely convert phone to Long
                    email = userDetail.email,
                    objectId = if (currentUser.contact.objectId.isNullOrEmpty()) userContactObjectId else currentUser.contact.objectId
                )
            )
            _currentUserState.emit(updatedUser)

        }
    }


    fun uploadLogoImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val file = ParseFile("logo.png", bytes)
                    file.save() // This uploads to Back4App

                    val logoUrl = file.url
                    if (logoUrl.isNotEmpty()) {
                        setLoading(false)
                    }
                    updateField { it.copy(logo = logoUrl) }


                }
            } catch (e: Exception) {
                setLoading(false)
                Log.e("UploadError", "Failed to upload logo", e)
            }
        }
    }

    fun uploadLogo(uri: Uri, context: Context) {
        viewModelScope.launch {
            setLoading(true)
            val responseUploadImage = userRepository.uploadImage(context, uri, "logo.png")
            when (responseUploadImage) {
                is APIResource.Success -> {
                    setLoading(false)
                    responseUploadImage.value?.let { logoUrl ->
                        updateField { it.copy(logo = logoUrl) }
                    } ?: _errorMessage.emit("Failed to upload logo")
                }

                is APIResource.Loading -> {
                    setLoading(true)
                }

                is APIResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(parseErrors(responseUploadImage))
                }

                is APIResource.ErrorString -> {
                    setLoading(false)
                    _errorMessage.emit(responseUploadImage.errorBody.toString())
                }

                else -> {

                }
            }

        }
    }

    fun uploadSelectedSignature(uri: Uri, context: Context) {
        viewModelScope.launch {
            setLoading(true)
            val responseUploadImage = userRepository.uploadImage(context, uri, "logo.png")
            when (responseUploadImage) {
                is APIResource.Success -> {
                    setLoading(false)
                    responseUploadImage.value?.let { signatureUrl ->
                        updateField { it.copy(signature = signatureUrl) }
                    } ?: _errorMessage.emit("Failed to upload logo")
                }

                is APIResource.Loading -> {
                    setLoading(true)
                }

                is APIResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(parseErrors(responseUploadImage))
                }

                is APIResource.ErrorString -> {
                    setLoading(false)
                    _errorMessage.emit(responseUploadImage.errorBody.toString())
                }

                else -> {

                }
            }

        }
    }


    fun uploadSignature(image: Bitmap, context: Context) {
        viewModelScope.launch {
            setLoading(true)
            val filename = "signature_${System.currentTimeMillis()}.png"
            val responseUploadImage = userRepository.uploadImage(context, image, filename)
            when (responseUploadImage) {
                is APIResource.Success -> {
                    setLoading(false)
                    responseUploadImage.value?.let { signature ->
                        updateField { it.copy(signature = signature) }
                    } ?: _errorMessage.emit("Failed to upload logo")
                }

                is APIResource.Loading -> {
                    setLoading(true)
                }

                is APIResource.Error -> {
                    setLoading(false)
                    _errorMessage.emit(parseErrors(responseUploadImage))
                }

                is APIResource.ErrorString -> {
                    setLoading(false)
                    _errorMessage.emit(responseUploadImage.errorBody.toString())
                }

                else -> {

                }
            }

        }
    }


    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }


    fun updateObjectIdToCurrentState(value: List<IdInfoRemoteResponse>) {
        viewModelScope.launch {
            var userObjectId: String? = null
            var userContactObjectId: String? = null
            var userAddressObjectId: String? = null
            value.forEach {
                when (it.table) {
                    SyncType.USER.type -> userObjectId = it.objectId
                    SyncType.CONTACT.type -> userContactObjectId = it.objectId

                    SyncType.ADDRESS.type -> userAddressObjectId = it.objectId

                    else -> {

                    }

                }
            }

            updateCurrentUserState(userObjectId, userContactObjectId, userAddressObjectId)
        }
    }
}


data class UserDetailState(
    val name: String = "",
    val country: String = "",
    val street: String = "",
    val suite: String = "",
    val postalCode: String = "",
    val city: String = "",
    val businessId: String = "",
    val cell: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val logo: String = "",
    val signature: String = "",
)

fun UserDetailState.toUserData(): UserData {
    return UserData(
        businessName = this.name,
        logo = this.logo, // Set appropriate value if available
        signature = this.signature, // Set appropriate value if available
        objectId = "",
        address = Address(
            street = this.street,
            aptSuite = this.suite,
            postalCode = this.postalCode,
            city = this.city,
            country = this.country
        ),
        contact = Contact(
            name = this.contactPerson,
            phone = this.phone.toLongOrNull() ?: 0L,
            cell = this.cell.toLongOrNull() ?: 0L,
            email = this.email,
            website = this.website
        ),
        syncStatus = SyncStatus.PENDING.status
    )


}


