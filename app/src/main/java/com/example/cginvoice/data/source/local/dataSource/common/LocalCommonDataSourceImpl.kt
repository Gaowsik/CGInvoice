package com.example.cginvoice.data.source.local.dataSource.common

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.local.dao.common.CommonDao
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity

class LocalCommonDataSourceImpl(private val commonDao: CommonDao) : LocalCommonDataSource,
    BaseRepo() {
    override suspend fun insertContactEntity(contactEntity: ContactEntity) = safeDbCall {
        commonDao.insertContactEntity(contactEntity)
    }

    override suspend fun insertAddressEntity(addressEntity: AddressEntity) = safeDbCall {
        commonDao.insertAddressEntity(addressEntity)
    }

    override suspend fun updateContactEntity(contactEntity: ContactEntity) = safeDbCall {
        commonDao.updateContactEntity(contactEntity)
    }

    override suspend fun updateAddressEntity(addressEntity: AddressEntity) = safeDbCall {
        commonDao.updateAddressEntity(addressEntity)
    }

    override suspend fun updateContactObjectId(
        contactId: Int,
        newObjectId: String
    ) = safeDbCall {
        commonDao.updateContactObjectId(contactId, newObjectId)
    }

    override suspend fun updateAddressObjectId(
        addressId: Int,
        newObjectId: String
    ) = safeDbCall {
        commonDao.updateAddressObjectId(addressId, newObjectId)
    }

    override suspend fun deleteContactEntity() = safeDbCall {
        commonDao.deleteContactEntity()
    }

    override suspend fun deleteAddressEntity() = safeDbCall {
        commonDao.deleteAddressEntity()
    }
}