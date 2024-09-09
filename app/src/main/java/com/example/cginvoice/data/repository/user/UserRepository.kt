package com.example.cginvoice.data.repository.user

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.IdInfoResponse
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

interface UserRepository {
    suspend fun insertUserInfoResponseToDB(userInfoResponse: UserInfoResponse)
    suspend fun getUserInfo(): DBResource<UserInfoResponse>
    suspend fun getUserInfoRemote(objectId : String): APIResource<UserInfoResponse>
    suspend fun createUserInfo(user: User, contact: Contact, address: Address)
    suspend fun updateUserInfoDB(user: UserInfoResponse)
    suspend fun userInfoSync(user: UserInfoResponse) : APIResource<List<IdInfoResponse>>
}