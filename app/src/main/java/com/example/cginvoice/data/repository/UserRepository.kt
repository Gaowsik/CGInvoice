package com.example.cginvoice.data.repository

import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

interface UserRepository {
    suspend fun getUserInfo(): User?
    suspend fun createUserInfo(user: User, contact: Contact, address: Address)
    suspend fun updateUserInfo(user: User)
}