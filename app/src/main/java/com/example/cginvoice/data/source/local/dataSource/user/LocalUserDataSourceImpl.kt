package com.example.cginvoice.data.source.local.dataSource.user

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dao.user.UserDao
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndAddressEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityAndContactEntity
import com.example.cginvoice.data.source.local.relation.user.UserEntityWithInvoiceEntities
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserAndAddress
import com.example.cginvoice.domain.model.user.UserAndContact
import com.example.cginvoice.domain.model.user.UserWithInvoices

class LocalUserDataSourceImpl(private val userDao: UserDao) : LocalUserDataSource, BaseRepo() {
    override suspend fun insertUserEntity(userEntity: UserEntity) = safeDbCall {
        userDao.insertUserEntity(userEntity)
    }

    override suspend fun updateUserEntity(userEntity: UserEntity) = safeDbCall {
        userDao.updateUserEntity(userEntity)
    }

    override suspend fun getUserAndContact(contactId: String) = safeDbCall {
        userDao.getUserEntityAndContactEntity(contactId).map {
            it.toUserAndContact()
        }
    }

    override suspend fun getUserAndAddress(addressId: String) = safeDbCall {
        userDao.getUserEntityAndAddressEntity(addressId).map {
            it.toUserAndAddress()
        }
    }

    override suspend fun getUserWithInvoices(userId: String) = safeDbCall {
        userDao.getUserEntityWithInvoiceEntities(userId).map {
            it.toUserWithInvoices()
        }
    }

    override suspend fun getUser() =safeDbCall {
        userDao.getUserEntity().toUser()
    }


    override suspend fun deleteUserEntity() = safeDbCall {
        userDao.deleteUserEntity()
    }


}