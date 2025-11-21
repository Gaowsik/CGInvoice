package com.example.cginvoice.domain.model.invoice

import com.example.cginvoice.data.source.remote.model.Invoice.PaymentResponse


data class Payment(
    val paymentId: Int?,
    val paymentObjectId: String?,
    val invoiceId: Long?=null,
    val paymentDate: String,
    val invoiceObjectId: String?=null,
    val amount: Double,
    val paymentMethod: String?,
    val note: String? = null
){
    fun toPaymentResponse(): PaymentResponse {
        return PaymentResponse(
            paymentId = paymentId,
            paymentObjectId = paymentObjectId,
            invoiceObjectId = invoiceObjectId,
            paymentDate = paymentDate,
            amount = amount,
            paymentMethod = paymentMethod,
            note = note
        )
    }
}