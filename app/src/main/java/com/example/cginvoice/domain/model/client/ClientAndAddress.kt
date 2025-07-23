package com.example.cginvoice.domain.model.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.domain.model.common.Address

data class ClientAndAddress(
    val client: Client,
    val address: Address
)