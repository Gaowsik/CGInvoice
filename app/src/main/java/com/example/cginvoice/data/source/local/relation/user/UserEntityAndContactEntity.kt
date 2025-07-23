package com.example.cginvoice.data.source.local.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserAndAddress
import com.example.cginvoice.domain.model.user.UserAndContact

data class UserEntityAndContactEntity(
    @Embedded val userEntity: UserEntity,
    @Relation(parentColumn = "contactId", entityColumn = "contactId")
    val contactEntity: ContactEntity
) {
    fun toUserAndContact(): UserAndContact {
        return UserAndContact(
            user = userEntity.toUser(), contact = contactEntity.toContact()
        )
    }
}