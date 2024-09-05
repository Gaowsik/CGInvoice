package com.example.cginvoice.data.repository.user

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.common.CommonRepository
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.common.toAddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.toContactEntity
import com.example.cginvoice.data.source.local.entitiy.user.UserEntity
import com.example.cginvoice.data.source.local.entitiy.user.toUserEntity
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.data.source.remote.user.RemoteUserDataSource
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource,
    private val localCommonDataSource: LocalCommonDataSource
) : UserRepository {
    override suspend fun insertUserInfoResponseToDB(userInfoResponse: UserInfoResponse) {
        val (userEntity, addressEntity, contactEntity) = userInfoResponse.toEntities()

        userEntity.let {
            localUserDataSource.insertUserEntity(userEntity)
        }

        addressEntity.let {
            localCommonDataSource.insertAddressEntity(addressEntity)
        }

        contactEntity.let {
            localCommonDataSource.insertContactEntity(contactEntity)
        }
    }

    override suspend fun getUserInfo(): DBResource<UserInfoResponse> {
        val response = localUserDataSource.getUser()
        if (response is DBResource.Success) {
            try {
                val contactResponse = localUserDataSource.getUserAndContact(response.value.contactId.toString())

                val addressResponse = localUserDataSource.getUserAndAddress(response.value.addressId.toString())

                if (contactResponse is DBResource.Success && addressResponse is DBResource.Success) {
                    val userInfoResponse = getUserResponse(
                        response.value,
                        contactResponse.value.first().contact,
                        addressResponse.value.first().address
                    )
                    return DBResource.Success(userInfoResponse)
                } else {
                    val errorException = when {
                        contactResponse is DBResource.Error -> contactResponse.exception
                        addressResponse is DBResource.Error -> addressResponse.exception
                        else -> Exception("Failed to fetch contact or address data.")
                    }
                    return DBResource.Error(errorException)
                }
            } catch (e: Exception) {
                return DBResource.Error(e)
            }
        } else if (response is DBResource.Error) {
            return DBResource.Error(response.exception)
        }
        return DBResource.Error(Exception("Unexpected error occurred while fetching user info."))
    }


    override suspend fun createUserInfo(user: User, contact: Contact, address: Address) {
        remoteUserDataSource.insertUserRemote(user, contact, address)
    }

    override suspend fun updateUserInfoDB(user: UserInfoResponse) {
        remoteUserDataSource.updateUserRemote(user)
    }

    override suspend fun userInfoSync(user: UserInfoResponse) {
        remoteUserDataSource.updateUserRemote(user)
    }

    suspend fun getUerRemote(userId: String) = remoteUserDataSource.getUserRemote(userId)


    private fun UserInfoResponse.toEntities(): Triple<UserEntity, AddressEntity, ContactEntity> {
        val userEntity = this.toUserEntity()
        val addressEntity = this.toAddressEntity()
        val contactEntity = this.toContactEntity()

        return Triple(userEntity, addressEntity, contactEntity)


    }

    private fun getUserResponse(user: User, contact: Contact, address: Address) =
        UserInfoResponse(
            userId = user.userId.toString(),
            businessName = user.businessName,
            logo = user.logo,
            signature = user.signature,
            objectId = user.objectId,
            address = address,
            contact = contact
        )


}


