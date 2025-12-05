package com.example.cginvoice.domain.model.invoice

import com.example.cginvoice.data.source.remote.model.Invoice.InvoiceResponse
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

data class Invoice(
    val invoiceId: Long = 0,
    val invoiceData: String = "",
    val dueDate: String = "",
    val invoiceObjectId: String = "",
    val totalAmount: Double = 0.0,
    val userId: Long = 0,
    val clientId: Long = 0,
    val imageId: String = "",
    val note: String = "",
    val paymentList: List<Payment> = emptyList(),
    val invoiceItemList: List<InvoiceItemData> = emptyList(),
    val syncStatus: String = ""
) {
    fun toInvoiceResponse(): InvoiceResponse {
        return InvoiceResponse(
            invoiceId = invoiceId.toInt(),
            invoiceData = invoiceData,
            dueDate = dueDate,
            invoiceObjectId = invoiceObjectId,
            totalAmount = totalAmount,
            userObjectId = userId.toString(),   // Assuming userId represents server objectId as String
            clientObjectId = clientId.toString(), // Same assumption for clientId
            imageId = imageId,
            note = note,
            paymentList = paymentList.map { it.toPaymentResponse() },
            invoiceItemList = invoiceItemList.map { it.toInvoiceItemResponse() }
        )
    }
}
