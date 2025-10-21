package com.example.cginvoice.data.source.local.entitiy.invoicItem

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

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
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val invoiceItemId: Int,
    val itemName: String,
    val description: String?,
    val defaultUnitPrice: Double,
    val defaultTax: Double,
    val defaultDiscount: Double,
    val invoiceId: Long,
    val quantity: Int,
    val syncStatus: String
) {

    fun toInvoiceItemData(): InvoiceItemData {
        return InvoiceItemData(
            invoiceItemId = invoiceItemId,
            itemName = itemName,
            invoiceId = invoiceId,
            description = description,
            defaultUnitPrice = defaultUnitPrice,
            defaultTax = defaultTax,
            defaultDiscount = defaultDiscount,
            quantity = quantity,
            syncStatus = syncStatus
        )
    }

}