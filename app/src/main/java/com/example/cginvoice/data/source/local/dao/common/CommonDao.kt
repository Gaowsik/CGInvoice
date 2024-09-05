package com.example.cginvoice.data.source.local.dao.common

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity

@Dao
interface CommonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactEntity(contactEntity: ContactEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddressEntity(addressEntity: AddressEntity)
    @Query("DELETE FROM ContactEntity")
    suspend fun deleteContactEntity()

    @Query("DELETE FROM AddressEntity")
    suspend fun deleteAddressEntity()
}