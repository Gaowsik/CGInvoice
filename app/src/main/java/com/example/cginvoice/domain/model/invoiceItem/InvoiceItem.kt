package com.example.cginvoice.domain.model.invoiceItem

data class InvoiceItem(
    val invoiceItemId: Long,
    val itemName: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val tax: Double,
    val discount: Double,
    val totalPrice: Double,
    val invoiceId: Long
)
