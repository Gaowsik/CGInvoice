package com.example.cginvoice.data.source.local.entitiy.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.common.Contact

@Entity
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val contactId: Int,
    val name: String,
    val phone: Long,
    val cell: Long,
    val email: String,
    val fax: String,
    val website: String,
    val objectId: String
) {

    // Method to convert ContactEntity to Contact
    fun toContact(): Contact {
        return Contact(
            contactId = contactId,
            name = name,
            phone = phone,
            cell = cell,
            email = email,
            fax = fax,
            website = website,
            objectId = objectId
        )
    }
}

fun Contact.toContactEntity(): ContactEntity {
    return ContactEntity(
        contactId = contactId,
        name = name,
        phone = phone,
        cell = cell,
        email = email,
        fax = fax,
        website = website,
        objectId = objectId
    )
}

