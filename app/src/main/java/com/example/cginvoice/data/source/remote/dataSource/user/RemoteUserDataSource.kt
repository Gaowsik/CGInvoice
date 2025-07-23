package com.example.cginvoice.data.source.remote.dataSource.user

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserData

interface RemoteUserDataSource {
    suspend fun insertUserRemote(user: User, contact: Contact, address : Address)

    suspend fun updateUserRemote(user: UserData) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun insertUserRemote(user: UserData) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun getUserRemote(userId : String): APIResource<UserData>

    suspend fun getFirstUserInfoRemote(): APIResource<UserData>

}