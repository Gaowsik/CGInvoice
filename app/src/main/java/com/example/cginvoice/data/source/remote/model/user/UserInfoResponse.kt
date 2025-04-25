package com.example.cginvoice.data.source.remote.model.user

import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.UserData
import com.example.cginvoice.utills.SyncStatus

data class UserInfoResponse(
    val userId: Int = 0,
    val businessName: String,
    val logo: String,
    val signature: String,
    val objectId: String? = "",
    val address: Address,
    val contact: Contact

)

fun UserInfoResponse.toUserEntity(addressId: Int, contactId: Int): UserEntity {
    return UserEntity(
        userId = userId,
        businessName = businessName,
        logo = logo,
        signature = signature,
        addressId = addressId,
        contactId = contactId,
        objectId = objectId ?: "",
        status = SyncStatus.COMPLETED.status
    )
}


fun UserInfoResponse.toUserData(): UserData {
    return UserData(
        userId = this.userId,
        businessName = this.businessName,
        logo = this.logo,
        signature = this.signature,
        objectId = this.objectId,
        address = this.address,
        contact = this.contact,
        syncStatus = SyncStatus.COMPLETED.status
    )
}
