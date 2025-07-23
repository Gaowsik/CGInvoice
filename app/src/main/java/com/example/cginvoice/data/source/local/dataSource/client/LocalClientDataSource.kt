package com.example.cginvoice.data.source.local.dataSource.client

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientAndAddress
import com.example.cginvoice.domain.model.client.ClientAndContact
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.client.ClientWithInvoices

interface LocalClientDataSource {
    suspend fun insertClientEntity(clientEntity: ClientEntity): DBResource<Long>
    suspend fun updateClientEntity(clientEntity: ClientEntity): DBResource<Unit>
    suspend fun getClientEntityAndAddressEntity(addressId: String): DBResource<List<ClientAndAddress>>
    suspend fun getClientEntityAndContactEntity(contactId: String): DBResource<List<ClientAndContact>>
    suspend fun getClientEntityWithInvoicesEntity(userId: String): DBResource<List<ClientWithInvoices>>
    suspend fun deleteClient(client: Client): DBResource<Unit>
    suspend fun deleteClientById(clientId: Int): DBResource<Unit>
    suspend fun updateClientObjectId(clientId: Int, newObjectId: String): DBResource<Unit>
    suspend fun getClientEntityById(clientId: Int): DBResource<Client>
    suspend fun getClients(): DBResource<List<Client>>
}