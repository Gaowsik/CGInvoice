package com.example.cginvoice.data.source.remote.model.Invoice

import com.example.cginvoice.domain.model.invoice.Payment

data class PaymentResponse(
    val paymentId: Int? = null,
    val paymentObjectId: String? = null,
    val invoiceObjectId: String? = null,
    val paymentDate: String,
    val amount: Double,
    val paymentMethod: String? = null,
    val note: String? = null
) {

    fun toPayment(): Payment {
        return Payment(
            paymentId = this.paymentId,
            paymentObjectId = this.paymentObjectId,
            invoiceObjectId = this.invoiceObjectId,
            paymentDate = this.paymentDate.toLong(),
            amount = this.amount,
            paymentMethod = this.paymentMethod,
            note = this.note
        )
    }
}