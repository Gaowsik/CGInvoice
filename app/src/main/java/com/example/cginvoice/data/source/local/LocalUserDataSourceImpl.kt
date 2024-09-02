package com.example.cginvoice.data.source.local

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.user.User

class LocalUserDataSourceImpl : LocalUserDataSource {
    override suspend fun insertUserDB(user: User): DBResource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserDB(user: User): DBResource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDB(): DBResource<User> {
        TODO("Not yet implemented")
    }


}