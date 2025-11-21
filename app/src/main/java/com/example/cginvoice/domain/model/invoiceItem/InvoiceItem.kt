package com.example.cginvoice.domain.model.invoiceItem

import com.example.cginvoice.data.source.remote.model.Invoice.InvoiceItemResponse
import com.example.cginvoice.utills.SyncStatus

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
) {
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
