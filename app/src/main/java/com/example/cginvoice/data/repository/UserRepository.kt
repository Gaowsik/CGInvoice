package com.example.cginvoice.data.repository

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

interface UserRepository {
    suspend fun getUserInfo(userId: String): APIResource<UserInfoResponse>
    suspend fun createUserInfo(user: User, contact: Contact, address: Address)
    suspend fun updateUserInfo(user: UserInfoResponse)
}