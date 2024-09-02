package com.example.cginvoice.data.source.remote.user

import androidx.compose.ui.graphics.vector.addPathNodes
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(private val back4AppUserManager: Back4AppUserManager) :
    RemoteUserDataSource, BaseRepo() {
    override suspend fun insertUserRemote(user: User, contact: Contact, adress: Address) =
        back4AppUserManager.insertUserInfo(user, adress, contact)

    override suspend fun updateUserRemote(user: User): APIResource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRemote(): APIResource<User> {
        TODO("Not yet implemented")
    }
}