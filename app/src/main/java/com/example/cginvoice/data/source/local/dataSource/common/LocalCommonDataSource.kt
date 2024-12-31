package com.example.cginvoice.data.source.local.dataSource.common

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity

interface LocalCommonDataSource {
    suspend fun insertContactEntity(contactEntity: ContactEntity) : DBResource<Long>

    suspend fun insertAddressEntity(addressEntity: AddressEntity) : DBResource<Long>

    suspend fun updateContactEntity(contactEntity: ContactEntity) : DBResource<Int>

    suspend fun updateAddressEntity(addressEntity: AddressEntity) : DBResource<Int>

    suspend fun updateContactObjectId(contactId: Int, newObjectId: String) : DBResource<Unit>

    suspend fun updateAddressObjectId(addressId: Int, newObjectId: String) : DBResource<Unit>

    suspend fun deleteContactEntity() : DBResource<Unit>

    suspend fun deleteAddressEntity() : DBResource<Unit>
}