package com.example.cginvoice.data.source.remote.model.Invoice

import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.utills.SyncStatus

data class InvoiceResponse(
    val invoiceId: Int = 0,
    val invoiceData: String,
    val dueDate: String,
    val invoiceObjectId: String,
    val totalAmount: Double,
    val userObjectId: String?,
    val clientObjectId: String?,
    val imageId: String = "",
    val note: String = "",
    val paymentStatus: Boolean = false,
    val paymentList: List<PaymentResponse> = emptyList(),
    val invoiceItemList: List<InvoiceItemResponse> = emptyList()
) {

    fun toInvoice(): Invoice {
        return Invoice(
            invoiceId = this.invoiceId.toLong(),
            invoiceData = this.invoiceData,
            dueDate = this.dueDate,
            invoiceObjectId = this.invoiceObjectId,
            totalAmount = this.totalAmount,
            userId = this.userObjectId?.toLong() ?: 0L,
            clientId = this.clientObjectId?.toLong() ?: 0L,
            imageId = this.imageId,
            note = this.note,
            paymentStatus = paymentStatus,
            paymentList = this.paymentList.map { it.toPayment() },
            invoiceItemList = this.invoiceItemList.map { it.toInvoiceItem() },
            syncStatus = SyncStatus.COMPLETED.status
        )
    }
}