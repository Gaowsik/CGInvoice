package com.example.cginvoice.data.source.local.entitiy.invoice

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.domain.model.invoice.Invoice

@Entity
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = false) val invoiceId: Long,
    val invoiceData: String,
    val dueDate: String,
    val totalAmount: Double,
    val userId: Long,
    val clientId: Long,
    val imageId: String,
    val note: String
) {

    // Method to convert InvoiceEntity to Invoice
    fun toInvoice(): Invoice {
        return Invoice(
            invoiceId = invoiceId,
            invoiceData = invoiceData,
            dueDate = dueDate,
            totalAmount = totalAmount,
            userId = userId,
            clientId = clientId,
            imageId = imageId,
            note = note
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
            note = note
        )
    }