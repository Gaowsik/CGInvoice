package com.example.cginvoice.data.source.local.dao.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cginvoice.data.source.local.entitiy.client.ClientEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.client.ClientEntityWithInvoicesEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityWithInvoiceEntities

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientEntity(clientEntity: ClientEntity): Long

    @Update
    suspend fun updateClientEntity(clientEntity: ClientEntity)

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE contactId = :contactId")
    suspend fun getClientEntityAndAddressEntity(contactId: String): List<ClientEntityAndAddressEntity>

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE addressId = :addressId")
    suspend fun getClientEntityAndContactEntity(addressId: String): List<ClientEntityAndContactEntity>

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE userId = :userId")
    suspend fun getClientEntityWithInvoicesEntity(userId: String): List<ClientEntityWithInvoicesEntity>

    @Query("DELETE FROM ClientEntity")
    suspend fun deleteUserEntity()

    @Query("UPDATE ClientEntity SET objectId = :newObjectId WHERE clientId = :clientId")
    suspend fun updateClientObjectId(clientId: Int, newObjectId: String)
}