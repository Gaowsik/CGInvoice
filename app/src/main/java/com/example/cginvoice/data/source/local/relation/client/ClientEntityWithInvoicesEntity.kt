package com.example.cginvoice.data.source.local.relation.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.domain.model.client.ClientAndContact
import com.example.cginvoice.domain.model.client.ClientWithInvoices

data class ClientEntityWithInvoicesEntity(
    @Embedded val clientEntity: ClientEntity,
    @Relation(
        parentColumn = "clientId", entityColumn = "clientId"
    )
    val invoiceEntityList: List<InvoiceEntity>
) {
    fun toClientWithInvoices(): ClientWithInvoices {
        return ClientWithInvoices(
            client = clientEntity.toClient(), invoiceList = invoiceEntityList.map { it.toInvoice() }
        )
    }

}