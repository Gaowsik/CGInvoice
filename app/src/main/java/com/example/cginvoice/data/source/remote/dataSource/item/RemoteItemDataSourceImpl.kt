package com.example.cginvoice.data.source.remote.dataSource.item

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.remote.back4AppManager.item.Back4AppItemManager
import com.example.cginvoice.data.source.remote.model.item.toItemData
import com.example.cginvoice.domain.model.item.ItemData
import javax.inject.Inject

class RemoteItemDataSourceImpl @Inject constructor(private val back4AppItemManager: Back4AppItemManager) :
    RemoteItemDataSource, BaseRepo() {
    override suspend fun updateItemRemote(item: ItemData) =
        back4AppItemManager.updateItem(item.toItemResponse())


    override suspend fun insertItemRemote(item: ItemData) =
        back4AppItemManager.insertItem(item.toItemResponse())

    override suspend fun getItemsRemoteByUserId(userId: String) = safeApiCall {
        back4AppItemManager.getAllItems(userId).map {
            it.toItemData()
        }
    }

    override suspend fun deleteItemRemote(itemObjectId: String, itemId: Int) =
        back4AppItemManager.deleteItem(itemObjectId, itemId)


}