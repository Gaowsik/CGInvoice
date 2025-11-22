package com.example.cginvoice.domain.model.invoice

import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
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

fun Payment.toPaymentEntity(invoiceIdGenerated: Long): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId?.toLong() ?: 0L, // if already assigned, keep it, else Room will auto-generate
        paymentObjectId = this.paymentObjectId,
        invoiceId = this.invoiceId ?: invoiceIdGenerated, // use generated invoiceId if null
        paymentDate = this.paymentDate,
        amount = this.amount,
        paymentMethod = this.paymentMethod,
        note = this.note
    )
}