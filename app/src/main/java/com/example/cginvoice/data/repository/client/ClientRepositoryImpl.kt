package com.example.cginvoice.data.repository.client

import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dataSource.client.LocalClientDataSource
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.common.toAddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.toContactEntity
import com.example.cginvoice.data.source.remote.dataSource.client.RemoteClientDataSource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.client.toClientEntity
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.client.toClientEntity
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.utills.SyncStatus
import com.example.cginvoice.utills.SyncType
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val remoteClientDataSource: RemoteClientDataSource,
    private val localClientDataSource: LocalClientDataSource,
    private val localCommonDataSource: LocalCommonDataSource
) : ClientRepository {
    override suspend fun getClientRemoteByUserId(userId: String) =
        remoteClientDataSource.getClientRemoteByUserId(userId)


    override suspend fun clientInfoSync(client: ClientData): APIResource<List<IdInfoRemoteResponse>> {
        return if (client.objectId.isNullOrEmpty()) {
            val response = remoteClientDataSource.insertClientRemote(client)
            if (response is APIResource.Success) {
                updateObjectId(response.value)
            }
            return response
        } else {
            remoteClientDataSource.updateClientRemote(client)
        }
    }

    override suspend fun insertClientInfoResponseToDB(clientInfoResponse: ClientInfoResponse): DBResource<Unit> {
        return try {
            val (addressEntity, contactEntity) = clientInfoResponse.toEntities()
            val responseAddress = localCommonDataSource.insertAddressEntity(addressEntity)
            val responseContact = localCommonDataSource.insertContactEntity(contactEntity)
            if (responseAddress is DBResource.Success && responseContact is DBResource.Success) {

                val clientEntity = clientInfoResponse.toClientEntity(
                    responseAddress.value.toInt(),
                    responseContact.value.toInt()
                )
                val responseClient = localClientDataSource.insertClientEntity(clientEntity)

                if (responseClient is DBResource.Success) {
                    DBResource.Success(Unit)
                } else {
                    DBResource.Error((responseClient as DBResource.Error).exception)
                }

            } else {
                val exception = when {
                    responseAddress is DBResource.Error -> responseAddress.exception
                    responseContact is DBResource.Error -> responseContact.exception
                    else -> Exception("Unknown error during insertion")
                }
                DBResource.Error(exception)
            }

        } catch (e: Exception) {
            DBResource.Error(e)
        }
    }

    override suspend fun getClientInfo(clientId: Int): DBResource<ClientData> {
        val response = localClientDataSource.getClientEntityById(clientId = clientId)
        if (response is DBResource.Success) {
            try {
                val contactResponse =
                    localClientDataSource.getClientEntityAndContactEntity(response.value.contactId.toString())

                val addressResponse =
                    localClientDataSource.getClientEntityAndAddressEntity(response.value.addressId.toString())

                if (contactResponse is DBResource.Success && addressResponse is DBResource.Success) {
                    val clientData = getClientData(
                        response.value,
                        contactResponse.value.first().contact,
                        addressResponse.value.first().address
                    )

                    return DBResource.Success(clientData)


                } else {
                    val errorException = when {
                        contactResponse is DBResource.Error -> contactResponse.exception
                        addressResponse is DBResource.Error -> addressResponse.exception
                        else -> Exception("Failed to fetch contact or address data.")
                    }
                    return DBResource.Error(errorException)
                }

            } catch (e: Exception) {

                return DBResource.Error(e)

            }


        } else if (response is DBResource.Error) {
            return DBResource.Error(response.exception)
        }
        return DBResource.Error(Exception("Unexpected error occurred while fetching user info."))

    }


    override suspend fun updateClientInfoDB(client: ClientData): DBResource<Unit> {
        return try {
            val responseAddress =
                localCommonDataSource.updateAddressEntity(client.address.toAddressEntity())
            val responseContact =
                localCommonDataSource.updateContactEntity(client.contact.toContactEntity())
            val responseClient = localClientDataSource.updateClientEntity(client.toClientEntity())
            if (responseAddress is DBResource.Success && responseContact is DBResource.Success && responseClient is DBResource.Success) {
                DBResource.Success(Unit)
            } else {
                val exception = when {
                    responseAddress is DBResource.Error -> responseAddress.exception
                    responseContact is DBResource.Error -> responseContact.exception
                    responseClient is DBResource.Error -> responseClient.exception
                    else -> Exception("Unknown error during insertion")
                }
                DBResource.Error(exception)
            }

        } catch (e: Exception) {
            DBResource.Error(e)
        }
    }

    override suspend fun deleteClientById(clientID: Int) {
        localClientDataSource.deleteClientById(clientID)
    }


    override suspend fun getClientsWithContactAndAddress(): DBResource<List<ClientData>> {
        val clientListResponse = localClientDataSource.getClients()
        if (clientListResponse is DBResource.Success) {

            val clientList = clientListResponse.value.map { clientData ->
                try {
                    val contactResponse =
                        localClientDataSource.getClientEntityAndContactEntity(clientData.contactId.toString())

                    val addressResponse =
                        localClientDataSource.getClientEntityAndAddressEntity(clientData.addressId.toString())

                    if (contactResponse is DBResource.Success && addressResponse is DBResource.Success) {
                        val clientData = getClientData(
                            clientData,
                            contactResponse.value.first().contact,
                            addressResponse.value.first().address
                        )

                        clientData


                    } else {
                        val errorException = when {
                            contactResponse is DBResource.Error -> contactResponse.exception
                            addressResponse is DBResource.Error -> addressResponse.exception
                            else -> Exception("Failed to fetch contact or address data.")
                        }
                        return DBResource.Error(errorException)
                    }


                } catch (e: Exception) {

                    return DBResource.Error(e)

                }

            }

            return DBResource.Success(clientList)

        } else if (clientListResponse is DBResource.Error) {
            return DBResource.Error(clientListResponse.exception)
        }
        return DBResource.Error(Exception("Unexpected error occurred while fetching user info."))

    }

    private fun ClientInfoResponse.toEntities(): Pair<AddressEntity, ContactEntity> {
        val addressEntity = this.toAddressEntity()
        val contactEntity = this.toContactEntity()
        return Pair(addressEntity, contactEntity)
    }

    private fun getClientData(client: Client, contact: Contact, address: Address) =
        ClientData(
            clientId = client.clientId,
            name = client.clientName,
            objectId = client.objectId,
            address = address,
            contact = contact
        )


    override suspend fun syncAllClients(clients: List<ClientData>) {
        clients.filter { it.syncStatus == SyncStatus.PENDING.status }.forEach { client ->
            val response = clientInfoSync(client)
            when (response) {
                is APIResource.Success -> {
                    Log.d("suc", "")
                }

                is APIResource.Error -> {
                    Log.d("suc", "")

                }

                APIResource.Loading -> {}
                is APIResource.ErrorString -> {
                    Log.d("suc", "")
                }
            }
        }
    }

    suspend fun updateObjectId(value: List<IdInfoRemoteResponse>) {
        value.forEach {
            when (it.table) {
                SyncType.CLIENT.type -> localClientDataSource.updateClientObjectId(
                    it.id,
                    it.objectId
                )

                SyncType.CONTACT.type -> localCommonDataSource.updateContactObjectId(
                    it.id,
                    it.objectId
                )

                SyncType.ADDRESS.type -> localCommonDataSource.updateAddressObjectId(
                    it.id,
                    it.objectId

                )

                else -> {

                }

            }
        }
    }


}