package com.example.cginvoice.data.source.local.relation.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.client.ClientAndAddress
import com.example.cginvoice.domain.model.user.UserAndAddress

data class ClientEntityAndAddressEntity(
    @Embedded val clientEntity: ClientEntity,
    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId"
    ) val addressEntity: AddressEntity
) {
    fun toUserAndAddress(): ClientAndAddress {
        return ClientAndAddress(
            client = clientEntity.toClient(), address = addressEntity.toAddress()
        )
    }
}