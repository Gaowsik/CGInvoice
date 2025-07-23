package com.example.cginvoice.data.source.remote.model.client

import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.utills.SyncStatus

data class ClientInfoResponse(
    val clientId: Int = 0,
    val name: String,
    val userInfoObjectId: String = "",
    val objectId: String? = "",
    val address: Address,
    val contact: Contact
)

fun ClientInfoResponse.toClientEntity(addressId: Int, contactId: Int): ClientEntity {
    return ClientEntity(
        clientId = clientId,
        clientName = name,
        addressId = addressId,
        contactId = contactId,
        userId = 0,
        objectId = objectId ?: "",
        status = SyncStatus.COMPLETED.status
    )
}

fun ClientInfoResponse.toClientData(): ClientData {
    return ClientData(
        clientId = clientId,
        name = name,
        userInfoObjectId = userInfoObjectId,
        objectId = objectId,
        address = address,
        contact = contact,
        syncStatus = SyncStatus.COMPLETED.status
    )
}
