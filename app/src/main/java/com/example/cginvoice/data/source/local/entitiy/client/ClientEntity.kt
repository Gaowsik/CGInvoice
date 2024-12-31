package com.example.cginvoice.data.source.local.entitiy.client

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact

@Entity
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val clientId: Int,
    val clientName: String,
    val addressId: Int,
    val contactId: Int,
    val userId: Int,
    val objectId: String,
    val status: String
) {
    fun toClient(): Client {
        return Client(
            clientId = clientId,
            clientName = clientName,
            addressId = addressId,
            contactId = contactId,
            userId = userId,
            objectId = objectId,
            status = status
        )
    }
}

fun Client.toClientEntity(): ClientEntity {
    return ClientEntity(
        clientId = clientId,
        clientName = clientName,
        addressId = addressId,
        contactId = contactId,
        userId = userId,
        objectId = objectId,
        status = status
    )
}

fun ClientEntity.toClientData(address: Address = Address(), contact: Contact = Contact()): ClientData {
    return ClientData(
        clientId = clientId,
        name = clientName,
        userInfoObjectId = objectId,
        objectId = objectId,
        address = address,
        contact = contact,
        syncStatus = status
    )
}

