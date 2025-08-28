package com.example.cginvoice.data.source.local.entitiy.item

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.item.ItemData
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.utills.SyncStatus

@Entity
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Int,
    val itemObjectId: String?,
    val itemName: String,
    val userObjectId: String?,
    val description: String?,
    val defaultUnitPrice: Double,
    val defaultTax: Double,
    val defaultDiscount: Double,
    val syncStatus: String
) {

    fun toItemData(): ItemData {
        return ItemData(
            itemId = itemId,
            itemObjectId = itemObjectId,
            itemName = itemName,
            userObjectId = userObjectId,
            description = description,
            defaultUnitPrice = defaultUnitPrice,
            defaultTax = defaultTax,
            defaultDiscount = defaultDiscount,
            syncStatus = syncStatus
        )

    }

}

fun ItemData.toItemEntity(): ItemEntity {
    return ItemEntity(
        itemId = itemId,
        itemObjectId = itemObjectId,
        itemName = itemName,
        userObjectId = userObjectId,
        description = description,
        defaultUnitPrice = defaultUnitPrice,
        defaultTax = defaultTax,
        defaultDiscount = defaultDiscount,
        syncStatus = syncStatus
    )
}



