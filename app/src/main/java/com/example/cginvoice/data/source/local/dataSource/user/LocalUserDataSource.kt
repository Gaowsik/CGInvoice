package com.example.cginvoice.data.source.local.dataSource.user

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityWithInvoiceEntities
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserAndAddress
import com.example.cginvoice.domain.model.user.UserAndContact
import com.example.cginvoice.domain.model.user.UserWithInvoices

interface LocalUserDataSource {
    suspend fun insertUserEntity(userEntity: UserEntity): DBResource<Long>
    suspend fun updateUserEntity(userEntity: UserEntity): DBResource<Unit>
    suspend fun getUserAndContact(contactId: String): DBResource<List<UserAndContact>>
    suspend fun getUserAndAddress(addressId: String): DBResource<List<UserAndAddress>>
    suspend fun getUserWithInvoices(userId: String): DBResource<List<UserWithInvoices>>
    suspend fun getUser() : DBResource<User>
    suspend fun deleteUserEntity(): DBResource<Unit>

}