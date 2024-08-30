package com.example.cginvoice.domain.model.payment

data class Payment(
    val paymentId: Long,
    val paymentDate: String,
    val amount: Double,
    val paymentMethod: String,
    val invoiceId: Long
)
