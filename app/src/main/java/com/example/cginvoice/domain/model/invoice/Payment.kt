package com.example.cginvoice.domain.model.invoice


data class Payment(
    val paymentId: Long,
    val invoiceId: Long,
    val paymentDate: String,
    val amount: Double,
    val paymentMethod: String?,
    val note: String? = null
)