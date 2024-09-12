package com.example.cginvoice.data.source.remote.dataSource.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse

interface RemoteClientDataSource {

    suspend fun updateClientRemote(client: ClientInfoResponse) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun insertClientRemote(client: ClientInfoResponse) : APIResource<List<IdInfoRemoteResponse>>

    suspend fun getClientRemoteByUserId(userId : String): APIResource<List<ClientInfoResponse>>
}