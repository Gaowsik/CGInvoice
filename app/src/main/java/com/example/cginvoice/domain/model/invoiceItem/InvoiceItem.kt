package com.example.cginvoice.domain.model.invoiceItem

import com.example.cginvoice.utills.SyncStatus

data class InvoiceItemData(
    val invoiceItemId: Int = 0,
    val invoiceItemObjectId: String? = null,
    val itemName: String = "",
    val invoiceId: Long? = null,
    val description: String? = null,
    val defaultUnitPrice: Double = 0.0,
    val defaultTax: Double = 0.0,
    val defaultDiscount: Double = 0.0,
    val quantity: Int = 1,
    val syncStatus: String = SyncStatus.PENDING.status
)
