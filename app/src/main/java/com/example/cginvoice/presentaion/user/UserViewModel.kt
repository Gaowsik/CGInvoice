package com.example.cginvoice.presentaion.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.utills.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _getUserInfo = MutableSharedFlow<UserInfoResponse>()
    val getUserInfo = _getUserInfo.asSharedFlow()

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

    /*    fun updateUserInfo(userInfoResponse: UserInfoResponse){
            viewModelScope.launch {
                remoteUserDataSource.updateUserRemote(userInfoResponse)
            }
        }*/


}