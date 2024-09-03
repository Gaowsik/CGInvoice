package com.example.cginvoice.data.source.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

data class UserAndContact(
    @Embedded val user: User,
    @Relation(parentColumn = "contactId", entityColumn = "contactId") val contact: Contact
)