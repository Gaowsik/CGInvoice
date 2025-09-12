package com.example.cginvoice.data.source.local.dataSource.item

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dao.item.ItemDao
import com.example.cginvoice.data.source.local.entitiy.item.toItemEntity
import com.example.cginvoice.domain.model.item.ItemData

class LocalItemDataSourceImpl(private val itemDao: ItemDao) : LocalItemDataSource, BaseRepo() {
    override suspend fun insertItemEntity(itemData: ItemData) = safeDbCall {
        itemDao.insertItemEntity(itemData.toItemEntity())
    }

    override suspend fun updateItemEntity(itemData: ItemData) = safeDbCall {
        itemDao.updateItemEntity(itemData.toItemEntity())
    }

    override suspend fun deleteItem(itemData: ItemData) = safeDbCall {
        itemDao.deleteItem(itemData.toItemEntity())
    }

    override suspend fun deleteItem(itemId: Int) = safeDbCall {
       itemDao.deleteItemByItemId(itemId)
    }

    override suspend fun getItem(itemId: Int) = safeDbCall {
        itemDao.getItem(itemId).toItemData()
    }

    override suspend fun getItems() = safeDbCall {
        itemDao.getItems().map { it.toItemData() }
    }

    override suspend fun updateItemObjectId(itemId: Int, objectId: String) = safeDbCall {
        itemDao.updateItemObjectId(itemId, objectId)
    }

    override suspend fun updateStatusByItemId(itemId: Int, status: String) = safeDbCall {
        itemDao.updateStatusByItemId(itemId, status)
    }
}