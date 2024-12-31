package com.example.cginvoice.data.source.remote.model.user

import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact

data class UserInfoResponse(
    val userId: Int = 0,
    val businessName: String,
    val logo: String,
    val signature: String,
    val objectId: String? = "",
    val address: Address,
    val contact: Contact

)

fun UserInfoResponse.toContactEntity(): ContactEntity {
    return ContactEntity(
        contactId = contact?.contactId ?: 0,  // Assuming Contact has an 'id' field as a String
        name = contact?.name ?: "",
        phone = contact?.phone ?: 0L,
        cell = contact?.cell ?: 0L,
        email = contact?.email ?: "",
        fax = contact?.fax ?: "",
        website = contact?.website ?: "",
        objectId = objectId ?: ""
    )

}
