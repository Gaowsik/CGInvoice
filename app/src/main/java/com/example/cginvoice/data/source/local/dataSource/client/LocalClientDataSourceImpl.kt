package com.example.cginvoice.data.source.local.dataSource.client

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dao.client.ClientDao
import com.example.cginvoice.data.source.local.dao.user.UserDao
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.client.toClientData
import com.example.cginvoice.data.source.local.entitiy.client.toClientEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityWithInvoicesEntity
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientAndAddress
import com.example.cginvoice.domain.model.client.ClientAndContact
import com.example.cginvoice.domain.model.client.ClientWithInvoices

class LocalClientDataSourceImpl(private val clientDao: ClientDao) : LocalClientDataSource,
    BaseRepo() {
    override suspend fun insertClientEntity(clientEntity: ClientEntity) = safeDbCall {
        clientDao.insertClientEntity(clientEntity)
    }

    override suspend fun updateClientEntity(clientEntity: ClientEntity) = safeDbCall {
        clientDao.updateClientEntity(clientEntity)
    }

    override suspend fun getClientEntityAndAddressEntity(addressId: String) = safeDbCall {
        clientDao.getClientEntityAndAddressEntity(addressId).map { it.toUserAndAddress() }
    }

    override suspend fun getClientEntityAndContactEntity(addressId: String) = safeDbCall {
        clientDao.getClientEntityAndContactEntity(addressId).map { it.toClientAndContact() }
    }

    override suspend fun getClientEntityWithInvoicesEntity(userId: String) = safeDbCall {
        clientDao.getClientEntityWithInvoicesEntity(userId).map {
            it.toClientWithInvoices()
        }
    }


    override suspend fun deleteClient(client: Client) = safeDbCall {
        clientDao.deleteClient(client.toClientEntity())
    }

    override suspend fun deleteClientById(clientId: Int) = safeDbCall {
      clientDao.deleteClientById(clientId)
    }

    override suspend fun updateClientObjectId(
        clientId: Int,
        newObjectId: String
    ) = safeDbCall {
        clientDao.updateClientObjectId(clientId, newObjectId)
    }

    override suspend fun getClientEntityById(clientId: Int) = safeDbCall {
        clientDao.getClientEntityById(clientId).toClient()
    }

    override suspend fun getClients() = safeDbCall {
        clientDao.getClients().map {
            it.toClient()
        }
    }
}