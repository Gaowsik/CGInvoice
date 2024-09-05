package com.example.cginvoice.data.source.local.relation.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.user.UserAndContact
import com.example.cginvoice.domain.model.user.UserWithInvoices

data class UserEntityWithInvoiceEntities(
    @Embedded
    val userEntity: UserEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val invoiceEntityList: List<InvoiceEntity>
) {

    fun toUserWithInvoices(): UserWithInvoices {
        return UserWithInvoices(
            user = userEntity.toUser(), invoiceList = invoiceEntityList.map {
                it.toInvoice()
            }
        )
    }


}
