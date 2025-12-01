package com.example.cginvoice.data.source.local.entitiy.invoice

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.invoice.Invoice

@Entity
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val invoiceId: Long,
    val invoiceData: String,
    val dueDate: String,
    val totalAmount: Double,
    val invoiceObjectId: String,
    val userId: Long,
    val clientId: Long,
    val imageId: String,
    val note: String,
    val syncStatus: String
) {

    fun toInvoice(): Invoice {
        return Invoice(
            invoiceId = invoiceId,
            invoiceData = invoiceData,
            dueDate = dueDate,
            totalAmount = totalAmount,
            userId = userId,
            invoiceObjectId = invoiceObjectId,
            clientId = clientId,
            imageId = imageId,
            note = note,
            syncStatus = syncStatus
        )
    }
}

// Extension function to convert Invoice to InvoiceEntity
fun Invoice.toInvoiceEntity(): InvoiceEntity {
    return InvoiceEntity(
        invoiceId = invoiceId,
        invoiceData = invoiceData,
        dueDate = dueDate,
        totalAmount = totalAmount,
        userId = userId,
        clientId = clientId,
        imageId = imageId,
        note = note,
        invoiceObjectId = invoiceObjectId,
        syncStatus = syncStatus
    )
}