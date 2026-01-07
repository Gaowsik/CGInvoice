package com.example.cginvoice.domain.model.invoice

import android.os.Parcelable
import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
import com.example.cginvoice.data.source.remote.model.Invoice.PaymentResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payment(
    val paymentId: Int? = null,
    val paymentObjectId: String? = null,
    val invoiceId: Long? = null,
    val paymentDate: Long? = null,
    val invoiceObjectId: String? = null,
    val amount: Double = 0.0,
    val paymentMethod: String? = null,
    val note: String? = null
) : Parcelable {
    fun toPaymentResponse(): PaymentResponse {
        return PaymentResponse(
            paymentId = paymentId,
            paymentObjectId = paymentObjectId,
            invoiceObjectId = invoiceObjectId,
            paymentDate = paymentDate.toString(),
            amount = amount,
            paymentMethod = paymentMethod,
            note = note
        )
    }
}

fun Payment.toPaymentEntity(invoiceIdGenerated: Long = 0L): PaymentEntity {
    return PaymentEntity(
        paymentId = this.paymentId?.toLong()
            ?: 0L, // if already assigned, keep it, else Room will auto-generate
        paymentObjectId = this.paymentObjectId,
        invoiceId = this.invoiceId ?: invoiceIdGenerated, // use generated invoiceId if null
        paymentDate = this.paymentDate.toString().toString(),
        amount = this.amount,
        paymentMethod = this.paymentMethod,
        note = this.note
    )
}