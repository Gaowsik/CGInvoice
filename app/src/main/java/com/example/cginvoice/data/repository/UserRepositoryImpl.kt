package com.example.cginvoice.data.repository

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.LocalUserDataSource
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {
    override suspend fun getUserInfo(userId: String) = getUerRemote(userId)

    override suspend fun createUserInfo(user: User, contact: Contact, address: Address) {
        remoteUserDataSource.insertUserRemote(user, contact, address)
    }

    override suspend fun updateUserInfo(user: UserInfoResponse) {
      remoteUserDataSource.updateUserRemote(user)
    }


    suspend fun getUerRemote(userId: String) = remoteUserDataSource.getUserRemote(userId)


}