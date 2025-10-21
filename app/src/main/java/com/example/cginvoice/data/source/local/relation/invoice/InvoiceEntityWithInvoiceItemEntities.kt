package com.example.cginvoice.data.source.local.relation.invoice

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.domain.model.invoice.InvoiceWithInvoiceItems

data class InvoiceEntityWithInvoiceItemEntities(

    @Embedded
    val invoiceEntity: InvoiceEntity,

    @Relation(
        parentColumn = "invoiceId", entityColumn = "invoiceId"
    )
    val invoiceItemEntityList: List<InvoiceItemEntity>
) {
    fun toInvoiceWithInvoiceItems(): InvoiceWithInvoiceItems {
        return InvoiceWithInvoiceItems(
            invoice = invoiceEntity.toInvoice(),
            invoiceItemList = invoiceItemEntityList.map { it.toInvoiceItemData() }
        )
    }
}
