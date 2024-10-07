package com.example.cginvoice.data.repository.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dataSource.client.LocalClientDataSource
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.entitiy.client.toClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.common.toAddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.toContactEntity
import com.example.cginvoice.data.source.local.entitiy.user.toUserEntity
import com.example.cginvoice.data.source.remote.dataSource.client.RemoteClientDataSource
import com.example.cginvoice.data.source.remote.dataSource.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.utills.SyncType
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val remoteClientDataSource: RemoteClientDataSource,
    private val localClientDataSource: LocalClientDataSource,
    private val localCommonDataSource: LocalCommonDataSource
) : ClientRepository {
    override suspend fun getClientRemoteByUserId(userId: String) =
        remoteClientDataSource.getClientRemoteByUserId(userId)

    override suspend fun clientInfoSync(client: ClientInfoResponse): APIResource<List<IdInfoRemoteResponse>> {
        return if (client.objectId.isNullOrEmpty()) {
            val response = remoteClientDataSource.insertClientRemote(client)
            if (response is APIResource.Success) {
                // updateObjectId(response.value)
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
                    responseContact.value.toInt(),
                    clientInfoResponse.userInfoObjectId.toInt()
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

    override suspend fun getClientInfo(clientId: Int): DBResource<ClientInfoResponse> {
        val response = localClientDataSource.getClientEntityById(clientId = clientId)
        if (response is DBResource.Success) {
            try {
                val contactResponse =
                    localClientDataSource.getClientEntityAndContactEntity(response.value.contactId.toString())

                val addressResponse =
                    localClientDataSource.getClientEntityAndAddressEntity(response.value.addressId.toString())

                if (contactResponse is DBResource.Success && addressResponse is DBResource.Success) {
                    val clientInfoResponse = getClientResponse(
                        response.value,
                        contactResponse.value.first().contact,
                        addressResponse.value.first().address
                    )

                    return DBResource.Success(clientInfoResponse)


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


    override suspend fun updateClientInfoDB(clientInfoResponse: ClientInfoResponse) {
        TODO("Not yet implemented")
    }


    override suspend fun deleteUserInfo() {
        TODO("Not yet implemented")
    }

    private fun ClientInfoResponse.toEntities(): Pair<AddressEntity, ContactEntity> {
        val addressEntity = this.toAddressEntity()
        val contactEntity = this.toContactEntity()
        return Pair(addressEntity, contactEntity)
    }

    private fun getClientResponse(client: Client, contact: Contact, address: Address) =
        ClientInfoResponse(
            clientId = client.clientId,
            name = client.clientName,
            objectId = client.objectId,
            address = address,
            contact = contact
        )


}