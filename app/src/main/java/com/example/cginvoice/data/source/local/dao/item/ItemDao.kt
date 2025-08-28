package com.example.cginvoice.data.source.local.dao.item

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.item.ItemEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityWithInvoicesEntity
import com.example.cginvoice.presentaion.nav.Item

interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: ItemEntity)

    @Update
    suspend fun updateItemEntity(itemEntity: ItemEntity)

    @Delete
    suspend fun deleteItem(itemEntity: ItemEntity)

    @Query("SELECT * FROM ItemEntity WHERE itemId = :itemId LIMIT 1")
    suspend fun getItem(itemId: Int): ItemEntity

    @Query("SELECT * FROM ItemEntity")
    suspend fun getItems(): List<ItemEntity>

    @Query("UPDATE ItemEntity SET syncStatus = :syncStatus WHERE itemId = :itemId")
    suspend fun updateStatusByItemId(itemId: Int, syncStatus: String)

    @Query("UPDATE ItemEntity SET itemObjectId = :objectId WHERE itemId = :itemId")
    suspend fun updateItemObjectId(itemId: Int, objectId: String)
}