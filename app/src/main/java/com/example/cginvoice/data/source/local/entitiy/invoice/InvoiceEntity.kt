package com.example.cginvoice.data.source.local.entitiy.invoice

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.invoice.Invoice

@Entity
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val invoiceId: Long,
    val invoiceData: String,
    val generatedDate: String,
    val dueDate: String,
    val totalAmount: Double,
    val invoiceObjectId: String,
    val userId: String,
    val clientId: Long,
    val clientName: String,
    val clientObjectId: String,
    val imageId: String,
    val note: String,
    val paymentStatus: Boolean,
    val syncStatus: String
) {

    fun toInvoice(): Invoice {
        return Invoice(
            invoiceId = invoiceId,
            invoiceData = invoiceData,
            generatedDate = generatedDate,
            dueDate = dueDate,
            totalAmount = totalAmount,
            userId = userId,
            clientObjectId = clientObjectId,
            invoiceObjectId = invoiceObjectId,
            clientId = clientId,
            clientName = clientName,
            imageId = imageId,
            note = note,
            paymentStatus = paymentStatus,
            syncStatus = syncStatus
        )
    }
}

// Extension function to convert Invoice to InvoiceEntity
fun Invoice.toInvoiceEntity(clientIdSync: Long = 0L, clientNameSync: String = "",paymentStatus: Boolean= false): InvoiceEntity {
    return InvoiceEntity(
        invoiceId = invoiceId,
        invoiceData = invoiceData,
        generatedDate = generatedDate,
        dueDate = dueDate,
        totalAmount = totalAmount,
        userId = userId,
        clientId = if (clientId == 0L) clientIdSync else clientId,
        imageId = imageId,
        note = note,
        paymentStatus = paymentStatus,
        invoiceObjectId = invoiceObjectId,
        syncStatus = syncStatus,
        clientObjectId = clientObjectId,
        clientName = if (clientName.isNullOrEmpty()) clientNameSync else clientName
    )
}