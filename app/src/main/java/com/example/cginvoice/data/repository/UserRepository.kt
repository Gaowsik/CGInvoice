package com.example.cginvoice.data.repository

import com.example.cginvoice.domain.model.user.User

interface UserRepository {
    suspend fun getUserInfo(): User?
    suspend fun createUserInfo()
    suspend fun updateUserInfo(user: User)
}