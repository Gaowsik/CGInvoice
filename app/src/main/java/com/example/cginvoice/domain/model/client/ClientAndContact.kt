package com.example.cginvoice.domain.model.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.domain.model.common.Contact

class ClientAndContact(
    val client: Client,
    val contact: Contact
)