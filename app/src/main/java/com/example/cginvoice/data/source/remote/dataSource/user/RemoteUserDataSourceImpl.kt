package com.example.cginvoice.data.source.remote.dataSource.user

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.remote.back4AppManager.user.Back4AppUserManager
import com.example.cginvoice.data.source.remote.model.user.toUserData
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserData
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(private val back4AppUserManager: Back4AppUserManager) :
    RemoteUserDataSource, BaseRepo() {
    override suspend fun insertUserRemote(user: User, contact: Contact, adress: Address) =
        back4AppUserManager.insertUserInfo(user, adress, contact)

    override suspend fun insertUserRemote(user: UserData) =
        back4AppUserManager.insertUserInfo(user.toUserInfoResponse())

    override suspend fun updateUserRemote(user: UserData) =
        back4AppUserManager.updateUserInfo(user.toUserInfoResponse())

    override suspend fun getUserRemote(userId: String) = safeApiCall {
        back4AppUserManager.getUserInfo(userId).toUserData()
    }

    override suspend fun getFirstUserInfoRemote() = safeApiCall{
        back4AppUserManager.getFirstUserInfo().toUserData()
    }
}