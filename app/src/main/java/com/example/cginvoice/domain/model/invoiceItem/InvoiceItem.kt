package com.example.cginvoice.domain.model.invoiceItem

import android.os.Parcelable
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.remote.model.Invoice.InvoiceItemResponse
import com.example.cginvoice.utills.SyncStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceItemData(
    val invoiceItemId: Int = 0,
    val invoiceItemObjectId: String? = null,
    val itemName: String? = null,
    val invoiceObjectId: String? = null,
    val invoiceId: Long? = null,
    val description: String? = null,
    val defaultUnitPrice: Double = 0.0,
    val defaultTax: Double? = null,
    val defaultDiscount: Double? = null,
    val quantity: Int = 1,
    val syncStatus: String = SyncStatus.PENDING.status
) : Parcelable {
    fun toInvoiceItemResponse(): InvoiceItemResponse {
        return InvoiceItemResponse(
            invoiceItemId = invoiceItemId,
            invoiceItemObjectId = invoiceItemObjectId,
            itemName = itemName,
            description = description,
            quantity = quantity,
            defaultUnitPrice = defaultUnitPrice,
            defaultTax = defaultTax,
            defaultDiscount = defaultDiscount,
            invoiceObjectId = invoiceObjectId
        )
    }
}

fun InvoiceItemData.toInvoiceItemEntity(invoiceIdGenerated: Long = 0L): InvoiceItemEntity {
    return InvoiceItemEntity(
        invoiceItemId = this.invoiceItemId,
        invoiceItemObjectId = this.invoiceItemObjectId,
        itemName = this.itemName ?: "",
        invoiceId = this.invoiceId ?: invoiceIdGenerated,
        description = this.description,
        defaultUnitPrice = this.defaultUnitPrice,
        defaultTax = this.defaultTax ?: 0.0,
        defaultDiscount = this.defaultDiscount ?: 0.0,
        quantity = this.quantity,
        syncStatus = this.syncStatus
    )
}


