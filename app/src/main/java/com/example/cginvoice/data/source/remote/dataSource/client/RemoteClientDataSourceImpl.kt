package com.example.cginvoice.data.source.remote.dataSource.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.back4AppClientManager.client.Back4AppClientManager
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import javax.inject.Inject

class RemoteClientDataSourceImpl @Inject constructor(private val back4AppClientManager: Back4AppClientManager) : RemoteClientDataSource {
    override suspend fun updateClientRemote(client: ClientInfoResponse) =back4AppClientManager.updateClientInfo(client)
    override suspend fun insertClientRemote(client: ClientInfoResponse): APIResource<List<IdInfoRemoteResponse>> = back4AppClientManager.insertClientInfo(client)
    override suspend fun getClientRemoteByUserId(userId: String) = back4AppClientManager.getClientsByUserId(userId)
}