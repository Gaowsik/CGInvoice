package com.example.cginvoice.data.source.remote.user

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

interface RemoteUserDataSource {
    suspend fun insertUserRemote(user: User, contact: Contact, address : Address)

    suspend fun updateUserRemote(user: UserInfoResponse) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun insertUserRemote(user: UserInfoResponse) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun getUserRemote(userId : String): APIResource<UserInfoResponse>

}