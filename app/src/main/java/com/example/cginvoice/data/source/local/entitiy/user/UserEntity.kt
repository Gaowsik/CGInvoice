package com.example.cginvoice.data.source.local.entitiy.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.user.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: Int,
    val contactId: Int,
    val objectId: String
) {

    // Method to convert UserEntity to User
    fun toUser(): User {
        return User(
            userId = userId,
            businessName = businessName,
            logo = logo,
            signature = signature,
            addressId = addressId,
            contactId = contactId,
            objectId = objectId
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
        objectId = objectId
    )
}

fun UserInfoResponse.toUserEntity(addressId: Int,contactId: Int): UserEntity {
    return UserEntity(
        userId = userId,
        businessName = businessName,
        logo = logo,
        signature = signature,
        addressId = addressId,
        contactId = contactId,
        objectId = objectId ?: ""
    )
}
