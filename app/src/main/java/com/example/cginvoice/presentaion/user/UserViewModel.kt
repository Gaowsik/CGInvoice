package com.example.cginvoice.presentaion.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val remoteUserDataSource: RemoteUserDataSource
) : ViewModel() {
     fun insertUserRemote(user: User, contact: Contact, address: Address) {
         viewModelScope.launch {
             remoteUserDataSource.insertUserRemote(user, contact, address)
         }

    }


}