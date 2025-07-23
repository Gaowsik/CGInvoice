package com.example.cginvoice.data.source.local.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserAndAddress

data class UserEntityAndAddressEntity(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId"
    ) val addressEntity: AddressEntity
) {
    fun toUserAndAddress(): UserAndAddress {
        return UserAndAddress(
            user = userEntity.toUser(), address = addressEntity.toAddress()
        )
    }
}
