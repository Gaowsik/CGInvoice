package com.example.cginvoice.domain.model.client

import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.utills.SyncStatus

data class ClientData(
    val clientId: Int = 0,
    val name: String,
    val userInfoObjectId: String = "",
    val objectId: String? = "",
    val address: Address,
    val contact: Contact,
    val syncStatus: String = SyncStatus.PENDING.status
) {
    fun toClientInfoResponse() = ClientInfoResponse(
        clientId = clientId,
        name = name,
        userInfoObjectId = userInfoObjectId,
        objectId = objectId,
        address = address,
        contact = contact
    )
}

fun ClientData.toClientEntity(): ClientEntity {
    return ClientEntity(
        clientId = clientId,
        clientName = name,
        addressId = address.addressId,
        contactId = contact.contactId,
        userId = 0,
        objectId = objectId ?: "",
        status = SyncStatus.PENDING.status
    )
}

