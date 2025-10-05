package com.example.cginvoice.data.source.local.dao.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.item.ItemEntity
import com.example.cginvoice.utills.SyncStatus

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: ItemEntity)

    @Update
    suspend fun updateItemEntity(itemEntity: ItemEntity)

    @Delete
    suspend fun deleteItem(itemEntity: ItemEntity)

    @Query("DELETE FROM ItemEntity WHERE itemId = :itemId")
    suspend fun deleteItemByItemId(itemId: Int)

    @Query("SELECT * FROM ItemEntity WHERE itemId = :itemId LIMIT 1")
    suspend fun getItem(itemId: Int): ItemEntity

    @Query("SELECT * FROM ItemEntity WHERE syncStatus != :deleteStatus")
    suspend fun getItems(deleteStatus: String = SyncStatus.DELETE.status): List<ItemEntity>


    @Query("UPDATE ItemEntity SET syncStatus = :syncStatus WHERE itemId = :itemId")
    suspend fun updateStatusByItemId(itemId: Int, syncStatus: String)

    @Query("UPDATE ItemEntity SET itemObjectId = :objectId WHERE itemId = :itemId")
    suspend fun updateItemObjectId(itemId: Int, objectId: String)
}