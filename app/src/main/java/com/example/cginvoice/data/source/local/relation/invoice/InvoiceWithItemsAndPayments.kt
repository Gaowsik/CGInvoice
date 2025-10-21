package com.example.cginvoice.data.source.local.relation.invoice

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
import com.example.cginvoice.domain.model.invoice.Invoice

data class InvoiceWithItemsAndPayments(
    @Embedded val invoice: InvoiceEntity,
    @Relation(parentColumn = "invoiceId", entityColumn = "invoiceId")
    val items: List<InvoiceItemEntity>,
    @Relation(parentColumn = "invoiceId", entityColumn = "invoiceId")
    val payments: List<PaymentEntity>
) {

    fun toInvoice(): Invoice {
        return Invoice(
            invoiceId = invoice.invoiceId,
            invoiceData = invoice.invoiceData,
            dueDate = invoice.dueDate,
            invoiceObjectId = invoice.invoiceObjectId,
            totalAmount = invoice.totalAmount,
            userId = invoice.userId,
            clientId = invoice.clientId,
            imageId = invoice.imageId,
            note = invoice.note,
            paymentList = payments.map { it.toPayment() },
            invoiceItemList = items.map { it.toInvoiceItemData() },
            syncStatus = invoice.syncStatus
        )

    }

}