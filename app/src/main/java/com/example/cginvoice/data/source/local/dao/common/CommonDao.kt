package com.example.cginvoice.data.source.local.dao.common

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity

@Dao
interface CommonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactEntity(contactEntity: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddressEntity(addressEntity: AddressEntity): Long

    @Update
    suspend fun updateAddressEntity(addressEntity: AddressEntity) : Int

    @Update
    suspend fun updateContactEntity(contactEntity: ContactEntity) : Int

    @Query("UPDATE ContactEntity SET objectId = :newObjectId WHERE contactId = :contactId")
    suspend fun updateContactObjectId(contactId: Int, newObjectId: String)

    @Query("UPDATE AddressEntity SET objectId = :newObjectId WHERE addressId = :addressId")
    suspend fun updateAddressObjectId(addressId: Int, newObjectId: String)

    @Query("DELETE FROM ContactEntity")
    suspend fun deleteContactEntity()

    @Query("DELETE FROM AddressEntity")
    suspend fun deleteAddressEntity()

}