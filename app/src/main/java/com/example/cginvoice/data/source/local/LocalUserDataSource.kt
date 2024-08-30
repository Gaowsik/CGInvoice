package com.example.cginvoice.data.source.local

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.user.User

interface LocalUserDataSource {
    suspend fun insertUserDB(user: User): DBResource<Unit>

    suspend fun updateUserDB(user: User): DBResource<Unit>

    suspend fun getUserDB(): DBResource<User>

}