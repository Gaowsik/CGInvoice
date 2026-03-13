package com.example.cginvoice.data.source.remote.model.Invoice

import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.utills.SyncStatus

data class InvoiceResponse(
    val invoiceId: Int = 0,
    val invoiceData: String,
    val generatedDate: String = "",
    val dueDate: String,
    val invoiceObjectId: String,
    val totalAmount: String,
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
            generatedDate = this.generatedDate,
            dueDate = this.dueDate,
            invoiceObjectId = this.invoiceObjectId,
            totalAmount = this.totalAmount.toDouble(),
            userId = this.userObjectId?:"",
            clientId = 0,
            clientObjectId = this.clientObjectId ?: "",
            imageId = this.imageId,
            note = this.note,
            paymentStatus = paymentStatus,
            paymentList = this.paymentList.map { it.toPayment() },
            invoiceItemList = this.invoiceItemList.map { it.toInvoiceItem() },
            syncStatus = SyncStatus.COMPLETED.status
        )
    }
}