package com.example.cginvoice.domain.model.item

import android.os.Parcelable
import com.example.cginvoice.data.source.remote.model.item.ItemResponse
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.utills.SyncStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemData(
    val itemId: Int = 0,
    val itemObjectId: String? = null,
    val itemName: String = "",
    val userObjectId: String? = null,
    val description: String? = null,
    val defaultUnitPrice: Double = 0.0,
    val defaultTax: Double = 0.0,
    val defaultDiscount: Double = 0.0,
    val defaultQuantity: Int = 1,
    val syncStatus: String = SyncStatus.PENDING.status
) : Parcelable {
    fun toItemResponse(): ItemResponse {
        return ItemResponse(
            itemId = itemId,
            itemObjectId = itemObjectId,
            itemName = itemName,
            description = description,
            defaultUnitPrice = defaultUnitPrice.toString(),
            defaultTax = defaultTax.toString(),
            userObjectId = userObjectId,
            defaultDiscount = defaultDiscount.toString()
        )
    }


}

fun ItemData.toInvoiceItemData(): InvoiceItemData {
    return InvoiceItemData(
        invoiceItemId = itemId,
        invoiceItemObjectId = itemObjectId,
        itemName = itemName,
        description = description,
        defaultUnitPrice = defaultUnitPrice,
        defaultTax = defaultTax,
        defaultDiscount = defaultDiscount,
        syncStatus = SyncStatus.PENDING.status,
        quantity = defaultQuantity,
        invoiceObjectId = null,
        invoiceId = null
    )


}


