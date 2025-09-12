package com.example.cginvoice.data.source.remote.dataSource.item

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.item.ItemData

interface RemoteItemDataSource {
    suspend fun updateItemRemote(item: ItemData): APIResource<IdInfoRemoteResponse>

    suspend fun insertItemRemote(item: ItemData): APIResource<IdInfoRemoteResponse>

    suspend fun getItemsRemoteByUserId(userId: String): APIResource<List<ItemData>>

    suspend fun deleteItemRemote(itemObjectId: String,itemId : Int): APIResource<IdInfoRemoteResponse>

}