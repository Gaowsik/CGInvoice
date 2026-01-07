package com.example.cginvoice.data.source.local.entitiy.invoice

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.utills.SyncStatus

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["invoiceId"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val paymentId: Long = 0,
    val paymentObjectId: String?,
    val invoiceId: Long,
    val paymentDate: String,
    val amount: Double,
    val paymentMethod: String?,
    val note: String? = null,
    val syncStatus: String = SyncStatus.PENDING.status
) {


    fun toPayment(): Payment {
        return Payment(
            paymentId = paymentId.toInt(),
            paymentObjectId = paymentObjectId,
            invoiceId = invoiceId,
            paymentDate = paymentDate.toLong(),
            amount = amount,
            paymentMethod = paymentMethod,
            note = note,
            syncStatus = syncStatus
        )
    }

}