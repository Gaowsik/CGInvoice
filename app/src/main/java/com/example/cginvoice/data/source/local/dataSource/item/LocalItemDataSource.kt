package com.example.cginvoice.data.source.local.dataSource.item

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.item.ItemData

interface LocalItemDataSource {

    suspend fun insertItemEntity(itemData: ItemData): DBResource<Unit>

    suspend fun updateItemEntity(itemData: ItemData): DBResource<Unit>

    suspend fun deleteItem(itemData: ItemData): DBResource<Unit>

    suspend fun deleteItem(itemId: Int): DBResource<Unit>

    suspend fun getItem(itemId: Int): DBResource<ItemData>

    suspend fun getItems(): DBResource<List<ItemData>>

    suspend fun updateItemObjectId(itemId: Int, objectId: String): DBResource<Unit>

    suspend fun updateStatusByItemId(itemId: Int, status: String): DBResource<Unit>
}