package com.example.cginvoice.data.source.remote

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.user.User

interface RemoteUserDataSource {
    suspend fun insertUserRemote(user: User): APIResource<Unit>

    suspend fun updateUserRemote(user: User): APIResource<Unit>

    suspend fun getUserRemote(): APIResource<User>
}