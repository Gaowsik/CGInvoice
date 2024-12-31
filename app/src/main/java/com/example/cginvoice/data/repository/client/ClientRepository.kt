package com.example.cginvoice.data.repository.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientData

interface ClientRepository {
    suspend fun getClientRemoteByUserId(userId: String): APIResource<List<ClientData>>
    suspend fun clientInfoSync(client: ClientData): APIResource<List<IdInfoRemoteResponse>>
    suspend fun insertClientInfoResponseToDB(clientInfoResponse: ClientInfoResponse): DBResource<Unit>
    suspend fun getClientInfo(clientId: Int): DBResource<ClientData>
    suspend fun updateClientInfoDB(clientData: ClientData): DBResource<Unit>
    suspend fun deleteClientById(clientID: Int)
    suspend fun getClientsWithContactAndAddress(): DBResource<List<ClientData>>
    suspend fun syncAllClients(clients: List<ClientData>)
}