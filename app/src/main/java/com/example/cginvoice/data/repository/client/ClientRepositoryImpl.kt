package com.example.cginvoice.data.repository.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.remote.dataSource.client.RemoteClientDataSource
import com.example.cginvoice.data.source.remote.dataSource.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.utills.SyncType
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val remoteClientDataSource: RemoteClientDataSource
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


    /*    suspend fun updateObjectId(value: List<IdInfoRemoteResponse>) {
            value.forEach {
                when (it.table) {
                    SyncType.CLIENT.type -> localUserDataSource.updateUserObjectId(it.id, it.objectId)
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


            }*/

}