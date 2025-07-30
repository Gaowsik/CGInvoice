package com.example.cginvoice.data.source.remote.model.item

import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.item.ItemData
import com.example.cginvoice.utills.SyncStatus


data class ItemResponse(
    val itemId: Int = 0,
    val itemObjectId: String?,
    val itemName: String,
    val userObjectId:String?,
    val description: String?,
    val defaultUnitPrice: Double,
    val defaultTax: Double,
    val defaultDiscount: Double
)

fun ItemResponse.toItemData(): ItemData {
    return ItemData(
        itemId = itemId,
        itemObjectId = itemObjectId,
        itemName = itemName,
        userObjectId = userObjectId,
        description = description,
        defaultUnitPrice = defaultUnitPrice,
        defaultTax = defaultTax,
        defaultDiscount = defaultDiscount,
        syncStatus = SyncStatus.COMPLETED.status

    )
}