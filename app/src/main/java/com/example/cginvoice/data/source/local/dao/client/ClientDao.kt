package com.example.cginvoice.data.source.local.dao.client

import androidx.room.Dao
import androidx.room.Delete
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

    @Delete
    suspend fun deleteClient(clientEntity: ClientEntity)

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE addressId = :addressId")
    suspend fun getClientEntityAndAddressEntity(addressId: String): List<ClientEntityAndAddressEntity>

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE contactId = :contactId")
    suspend fun getClientEntityAndContactEntity(contactId: String): List<ClientEntityAndContactEntity>

    @Transaction
    @Query("SELECT * FROM ClientEntity WHERE clientId = :clientId")
    suspend fun getClientEntityWithInvoicesEntity(clientId: String): List<ClientEntityWithInvoicesEntity>

    @Query("DELETE FROM ClientEntity WHERE clientId = :clientId")
    suspend fun deleteClientById(clientId: Int)

    @Query("UPDATE ClientEntity SET objectId = :newObjectId WHERE clientId = :clientId")
    suspend fun updateClientObjectId(clientId: Int, newObjectId: String)

    @Query("SELECT * FROM ClientEntity WHERE clientId = :clientId LIMIT 1")
    suspend fun getClientEntityById(clientId: Int): ClientEntity

    @Query("SELECT * FROM ClientEntity")
    suspend fun getClients(): List<ClientEntity>
}