package com.example.cginvoice.data.source.local.relation.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientAndAddress
import com.example.cginvoice.domain.model.client.ClientAndContact

data class ClientEntityAndContactEntity(
    @Embedded val clientEntity: ClientEntity,
    @Relation(parentColumn = "contactId", entityColumn = "contactId")
    val contactEntity: ContactEntity
) {
    fun toClientAndContact(): ClientAndContact {
        return ClientAndContact(
            client = clientEntity.toClient(), contact = contactEntity.toContact()
        )
    }
}