package com.example.cginvoice.data.source.local.dataSource.client

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityWithInvoicesEntity
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientAndAddress
import com.example.cginvoice.domain.model.client.ClientAndContact
import com.example.cginvoice.domain.model.client.ClientWithInvoices

interface LocalClientDataSource {

    suspend fun insertClientEntity(clientEntity: ClientEntity) : DBResource<Long>
    suspend fun updateClientEntity(clientEntity: ClientEntity): DBResource<Unit>

    suspend fun getClientEntityAndAddressEntity(contactId: String): DBResource<List<ClientAndAddress>>

    suspend fun getClientEntityAndContactEntity(addressId: String): DBResource<List<ClientAndContact>>

    suspend fun getClientEntityWithInvoicesEntity(userId: String): DBResource<List<ClientWithInvoices>>

    suspend fun deleteUserEntity(): DBResource<Unit>

    suspend fun updateClientObjectId(clientId: Int, newObjectId: String): DBResource<Unit>

    suspend fun getClientEntityById(clientId : Int) : DBResource<Client>
}