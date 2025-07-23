package com.example.cginvoice.domain.model.user

import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.presentaion.user.UserDetailState
import com.example.cginvoice.utills.SyncStatus

data class UserData(
    val userId: Int = 0,
    val businessName: String="",
    val logo: String="",
    val signature: String="",
    val objectId: String? = "",
    val address: Address = Address(),
    val contact: Contact = Contact(),
    val syncStatus: String = SyncStatus.COMPLETED.status
) {
    fun toUserInfoResponse(): UserInfoResponse {
        return UserInfoResponse(
            userId = this.userId,
            businessName = this.businessName,
            logo = this.logo,
            signature = this.signature,
            objectId = this.objectId,
            address = this.address,
            contact = this.contact
        )
    }
}


fun UserData.toUserEntity(addressId: Int=0, contactId: Int=0): UserEntity {
    return UserEntity(
        userId = this.userId,
        businessName = this.businessName,
        logo = this.logo,
        signature = this.signature,
        addressId = if (addressId == 0) this.address.addressId else addressId,
        contactId = if (contactId == 0) this.contact.contactId else contactId,
        objectId = this.objectId ?: "",
        status = this.syncStatus
    )
}

fun UserData.toUserDetailState(): UserDetailState {
    return UserDetailState(
        name = businessName,
        logo = logo,
        signature = signature,
        country = address.country,
        street = address.street,
        suite = address.aptSuite,
        postalCode = address.postalCode,
        city = address.city,
        businessId = userId.toString(),
        cell = contact.cell.toString(),
        contactPerson = contact.name,
        phone = contact.phone.toString(),
        email = contact.email,
        website = contact.website
    )
}










