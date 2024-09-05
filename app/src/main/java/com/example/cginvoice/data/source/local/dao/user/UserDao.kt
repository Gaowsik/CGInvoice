package com.example.cginvoice.data.source.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityWithInvoiceEntities
import com.example.cginvoice.domain.model.invoice.Invoice

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEntity(userEntity: UserEntity)

    @Update
    suspend fun updateUserEntity(userEntity: UserEntity)

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE contactId = :contactId")
    suspend fun getUserEntityAndContactEntity(contactId: String): List<UserEntityAndContactEntity>

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE addressId = :addressId")
    suspend fun getUserEntityAndAddressEntity(addressId: String): List<UserEntityAndAddressEntity>

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE userId = :userId")
    suspend fun getUserEntityWithInvoiceEntities(userId: String): List<UserEntityWithInvoiceEntities>

    @Query("DELETE FROM UserEntity")
    suspend fun deleteUserEntity()
}