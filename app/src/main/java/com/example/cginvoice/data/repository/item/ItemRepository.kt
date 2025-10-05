package com.example.cginvoice.data.repository.item

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.item.ItemData

interface ItemRepository {
    suspend fun getItemList(): DBResource<List<ItemData>>
    suspend fun getItemInfo(itemId: Int): DBResource<ItemData>
    suspend fun insertOrUpdateItemInfoDB(itemData: ItemData): DBResource<Unit>
    suspend fun deleteItem(itemData: ItemData)
    suspend fun syncAllItems(itemData: List<ItemData>): APIResource<List<IdInfoRemoteResponse>>
}