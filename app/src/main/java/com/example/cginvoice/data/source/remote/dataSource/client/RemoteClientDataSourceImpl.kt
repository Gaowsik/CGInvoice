package com.example.cginvoice.data.source.remote.dataSource.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.remote.back4AppClientManager.client.Back4AppClientManager
import com.example.cginvoice.data.source.remote.model.client.toClientData
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.ClientData
import javax.inject.Inject

class RemoteClientDataSourceImpl @Inject constructor(private val back4AppClientManager: Back4AppClientManager) :
    RemoteClientDataSource,
    BaseRepo() {
    override suspend fun updateClientRemote(client: ClientData) =
        back4AppClientManager.updateClientInfo(client.toClientInfoResponse())

    override suspend fun insertClientRemote(client: ClientData): APIResource<List<IdInfoRemoteResponse>> =
        back4AppClientManager.insertClientInfo(client.toClientInfoResponse())

    override suspend fun getClientRemoteByUserId(userId: String) =
        safeApiCall {
            back4AppClientManager.getClientsByUserId(userId).map { it.toClientData() }
        }
}