package com.example.cginvoice.domain.model.invoice

data class Invoice(
    val invoiceId: Long,
    val invoiceData: String,
    val dueDate: String,
    val totalAmount: Double,
    val userId: Long,
    val clientId: Long,
    val imageId: String,
    val note: String
)
