package com.example.cginvoice.domain.model.invoice

import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

data class Invoice(
    val invoiceId: Long,
    val invoiceData: String,
    val dueDate: String,
    val invoiceObjectId: String,
    val totalAmount: Double,
    val userId: Long,
    val clientId: Long,
    val imageId: String,
    val note: String,
    val paymentList: List<Payment> = emptyList(),
    val invoiceItemList: List<InvoiceItemData> = emptyList(),
    val syncStatus: String
)
