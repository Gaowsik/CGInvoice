package com.example.cginvoice.data.source.remote.dataSource.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.ClientData

interface RemoteClientDataSource {

    suspend fun updateClientRemote(client: ClientData): APIResource<List<IdInfoRemoteResponse>>

    suspend fun insertClientRemote(client: ClientData): APIResource<List<IdInfoRemoteResponse>>

    suspend fun getClientRemoteByUserId(userId: String): APIResource<List<ClientData>>
}