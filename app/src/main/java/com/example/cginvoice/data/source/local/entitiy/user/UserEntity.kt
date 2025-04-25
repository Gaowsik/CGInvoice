package com.example.cginvoice.data.source.local.entitiy.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.utills.SyncStatus

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: Int,
    val contactId: Int,
    val objectId: String,
    val status: String
) {
    fun toUser(): User {
        return User(
            userId = userId,
            businessName = businessName,
            logo = logo,
            signature = signature,
            addressId = addressId,
            contactId = contactId,
            objectId = objectId,
            status = status
        )
    }
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        businessName = businessName,
        logo = logo,
        signature = signature,
        addressId = addressId,
        contactId = contactId,
        objectId = objectId,
        status = status
    )
}


