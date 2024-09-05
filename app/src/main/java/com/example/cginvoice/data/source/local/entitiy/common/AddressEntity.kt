package com.example.cginvoice.data.source.local.entitiy.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address

@Entity
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val addressId: Int,
    val country: String,
    val street: String,
    val aptSuite: String,
    val postalCode: String,
    val city: String,
    val objectId: String
) {

    // Method to convert AddressEntity to Address
    fun toAddress(): Address {
        return Address(
            addressId = addressId,
            country = country,
            street = street,
            aptSuite = aptSuite,
            postalCode = postalCode,
            city = city,
            objectId = objectId
        )
    }
}

fun Address.toAddressEntity(): AddressEntity {
    return AddressEntity(
        addressId = addressId,
        country = country,
        street = street,
        aptSuite = aptSuite,
        postalCode = postalCode,
        city = city,
        objectId = objectId
    )
}

fun UserInfoResponse.toAddressEntity(): AddressEntity {
    return AddressEntity(
        addressId = address?.addressId ?: 0,  // Assuming Address has an 'id' field as a String
        country = address?.country ?: "",
        street = address?.street ?: "",
        aptSuite = address?.aptSuite ?: "",
        postalCode = address?.postalCode ?: "",
        city = address?.city ?: "",
        objectId = objectId ?: ""
    )
}


