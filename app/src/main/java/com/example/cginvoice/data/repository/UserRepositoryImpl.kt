package com.example.cginvoice.data.repository

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.LocalUserDataSource
import com.example.cginvoice.data.source.remote.RemoteUserDataSource
import com.example.cginvoice.domain.model.user.User
import javax.inject.Inject

class UserRepositoryImpl@Inject constructor(private val localUserDataSource: LocalUserDataSource,private val remoteUserDataSource: RemoteUserDataSource) : UserRepository {
    override suspend fun getUserInfo(): User? {
        val response =
            localUserDataSource.getUserDB()
        if (response is DBResource.Success) {
            return response.value
        }
        else
        {
            getUerRemote()
        }
        return null
    }

    override suspend fun createUserInfo() {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserInfo(user: User) {
        TODO("Not yet implemented")
    }


   suspend fun getUerRemote() : User? {
       val response =  remoteUserDataSource.getUserRemote()
       if (response is APIResource.Success) {
           return response.value
       }
       return null
    }


}