package com.example.cginvoice.domain.model.item

import com.example.cginvoice.data.source.remote.model.item.ItemResponse
import com.example.cginvoice.utills.SyncStatus

data class ItemData(
    val itemId: Int = 0,
    val itemObjectId: String? = null,
    val itemName: String,
    val userObjectId: String?,
    val description: String? = null,
    val defaultUnitPrice: Double = 0.0,
    val defaultTax: Double = 0.0,
    val defaultDiscount: Double = 0.0,
    val syncStatus : String =  SyncStatus.PENDING.status
) {
    fun toItemResponse(): ItemResponse {
        return ItemResponse(
            itemId = itemId,
            itemObjectId = itemObjectId,
            itemName = itemName,
            description = description,
            defaultUnitPrice = defaultUnitPrice,
            defaultTax = defaultTax,
            userObjectId = userObjectId,
            defaultDiscount = defaultDiscount
        )
    }


}


