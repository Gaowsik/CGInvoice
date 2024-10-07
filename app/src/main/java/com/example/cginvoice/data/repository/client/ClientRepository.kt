package com.example.cginvoice.data.repository.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse

interface ClientRepository {
    suspend fun getClientRemoteByUserId(userId : String): APIResource<List<ClientInfoResponse>>

    suspend fun clientInfoSync(user: ClientInfoResponse) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun insertClientInfoResponseToDB(clientInfoResponse: ClientInfoResponse) : DBResource<Unit>

    suspend fun getClientInfo(clientId : Int): DBResource<ClientInfoResponse>

    suspend fun updateClientInfoDB(clientInfoResponse: ClientInfoResponse)

    suspend fun deleteUserInfo()
}