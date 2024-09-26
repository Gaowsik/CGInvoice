package com.example.cginvoice.data.source.local.entitiy.client

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.client.Client

@Entity
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val clientId: Int,
    val clientName: String,
    val addressId: Int,
    val contactId: Int,
    val userId: Int,
    val objectId: String
) {
    fun toClient(): Client {
        return Client(
            clientId = clientId,
            clientName = clientName,
            addressId = addressId,
            contactId = contactId,
            userId = userId,
            objectId = objectId
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
        objectId = objectId
    )
}

fun ClientInfoResponse.toClientEntity(addressId: Int, contactId: Int, userId: Int): ClientEntity {
    return ClientEntity(
        clientId = clientId,
        clientName = name,
        addressId = addressId,
        contactId = contactId,
        userId = userId,
        objectId = objectId ?: ""
    )
}

