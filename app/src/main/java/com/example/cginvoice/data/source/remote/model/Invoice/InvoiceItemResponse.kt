package com.example.cginvoice.data.source.remote.model.Invoice

import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

data class InvoiceItemResponse(
    val invoiceItemId: Int = 0,
    val invoiceItemObjectId: String? = null,
    val itemName: String?,
    val description: String?,
    val quantity: Int,
    val defaultUnitPrice: Double,
    val defaultTax: Double?,
    val defaultDiscount: Double?,
    val invoiceObjectId: String?
){
    fun toInvoiceItem() : InvoiceItemData {
        return InvoiceItemData(
            invoiceItemId = this.invoiceItemId,
            invoiceItemObjectId = this.invoiceItemObjectId,
            itemName = this.itemName,
            description = this.description,
            quantity = this.quantity,
            defaultUnitPrice = this.defaultUnitPrice,
            defaultTax = this.defaultTax,
            defaultDiscount = this.defaultDiscount,
            invoiceObjectId = this.invoiceObjectId
        )
    }
}
