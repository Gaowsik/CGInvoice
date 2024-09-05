package com.example.cginvoice.domain.model.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.domain.model.common.Contact

data class UserAndContact(
    val user: User,
    val contact: Contact
)