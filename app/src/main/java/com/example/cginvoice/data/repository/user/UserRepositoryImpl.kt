package com.example.cginvoice.data.repository.user

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dataSource.common.LocalCommonDataSource
import com.example.cginvoice.data.source.local.dataSource.user.LocalUserDataSource
import com.example.cginvoice.data.source.local.entitiy.common.AddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.ContactEntity
import com.example.cginvoice.data.source.local.entitiy.common.toAddressEntity
import com.example.cginvoice.data.source.local.entitiy.common.toContactEntity
import com.example.cginvoice.data.source.remote.dataSource.common.RemoteCommonDataSource
import com.example.cginvoice.data.source.remote.dataSource.user.RemoteUserDataSource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.data.source.remote.model.user.toUserEntity
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserData
import com.example.cginvoice.domain.model.user.toUserEntity
import com.example.cginvoice.utills.SyncType
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource,
    private val localCommonDataSource: LocalCommonDataSource,
    private val remoteCommonDataSource: RemoteCommonDataSource
) : UserRepository {

    override suspend fun insertUserInfoResponseToDB(userInfoResponse: UserInfoResponse): DBResource<Unit> {
        return try {
            val (addressEntity, contactEntity) = userInfoResponse.toEntities()
            val responseAddress = localCommonDataSource.insertAddressEntity(addressEntity)
            val responseContact = localCommonDataSource.insertContactEntity(contactEntity)

            if (responseAddress is DBResource.Success && responseContact is DBResource.Success) {

                val userEntity = userInfoResponse.toUserEntity(
                    responseAddress.value.toInt(),
                    responseContact.value.toInt()
                )

                val responseUser = localUserDataSource.insertUserEntity(userEntity)

                if (responseUser is DBResource.Success) {
                    DBResource.Success(Unit)
                } else {
                    DBResource.Error((responseUser as DBResource.Error).exception)
                }
            } else {
                val exception = when {
                    responseAddress is DBResource.Error -> responseAddress.exception
                    responseContact is DBResource.Error -> responseContact.exception
                    else -> Exception("Unknown error during insertion")
                }
                DBResource.Error(exception)
            }
        } catch (e: Exception) {
            DBResource.Error(e)
        }
    }

    override suspend fun getUserInfoDB(): DBResource<UserData> {
        val response = localUserDataSource.getUser()
        if (response is DBResource.Success) {
            try {
                val contactResponse =
                    localUserDataSource.getUserAndContact(response.value.contactId.toString())

                val addressResponse =
                    localUserDataSource.getUserAndAddress(response.value.addressId.toString())

                if (contactResponse is DBResource.Success && addressResponse is DBResource.Success) {
                    val userDataResponse = getUserData(
                        response.value,
                        contactResponse.value.first().contact,
                        addressResponse.value.first().address
                    )
                    return DBResource.Success(userDataResponse)
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

    override suspend fun getUserInfo(): DBResource<UserData> {
        val response = getUserInfoDB()
        if (response is DBResource.Success) {
            return response
        } else if (response is DBResource.Error) {
            val response = getAndSaveUserInfoRemote()
            if (response is DBResource.Success) {
                return getUserInfoDB()
            }
        }
        return DBResource.Error(Exception())
    }


    override suspend fun getAndSaveUserInfoRemote(): DBResource<Unit> {
        val response = remoteUserDataSource.getFirstUserInfoRemote()
        if (response is APIResource.Success) {
            val responseInsert = insertUserInfoResponseToDB(response.value.toUserInfoResponse())
            if (responseInsert is DBResource.Success) {
                return DBResource.Success(Unit)
            }

        } else if (response is APIResource.Error) {
            return DBResource.Error(Exception(response.errorBody.toString()))
        }
        return DBResource.Error(Exception())

    }


    override suspend fun getFirstUserInfoRemote() = remoteUserDataSource.getFirstUserInfoRemote()


    override suspend fun createUserInfo(user: User, contact: Contact, address: Address) {
        remoteUserDataSource.insertUserRemote(user, contact, address)
    }

    override suspend fun updateUserInfoDB(user: UserData): DBResource<Unit> {
        return try {
            val responseAddress =
                localCommonDataSource.updateAddressEntity(user.address.toAddressEntity())

            val responseContact =
                localCommonDataSource.updateContactEntity(user.contact.toContactEntity())

            val responseUser = localUserDataSource.updateUserEntity(user.toUserEntity())
            if (responseAddress is DBResource.Success && responseContact is DBResource.Success && responseUser is DBResource.Success) {
                DBResource.Success(Unit)
            } else {
                val exception = when {
                    responseAddress is DBResource.Error -> responseAddress.exception
                    responseContact is DBResource.Error -> responseContact.exception
                    responseUser is DBResource.Error -> responseUser.exception
                    else -> Exception("Unknown error during insertion")
                }
                DBResource.Error(exception)
            }

        } catch (e: Exception) {
            DBResource.Error(e)
        }
    }

    override suspend fun insertUserInfoDB(user: UserData): DBResource<Unit> {
        try {

            val responseContact =
                localCommonDataSource.insertContactEntity(user.contact.toContactEntity())
            val responseAddress =
                localCommonDataSource.insertAddressEntity(user.address.toAddressEntity())

            if (responseAddress is DBResource.Success && responseContact is DBResource.Success) {
                val userEntity =
                    user.toUserEntity(responseAddress.value.toInt(), responseContact.value.toInt())
                val responseUser = localUserDataSource.insertUserEntity(userEntity)
                if (responseUser is DBResource.Success) {
                    DBResource.Success(Unit)
                } else {
                    DBResource.Error((responseUser as DBResource.Error).exception)
                }

                return DBResource.Success(Unit)
                return DBResource.Success(Unit)
            } else {
                val exception = when {
                    responseAddress is DBResource.Error -> responseAddress.exception
                    responseContact is DBResource.Error -> responseContact.exception
                    else -> Exception("Unknown error during insertion")
                }
                return DBResource.Error(exception)
            }

        } catch (e: Exception) {
            return DBResource.Error(e)
        }
    }

    override suspend fun updateStatusByUserID(userId: Int, status: String) =
        localUserDataSource.updateStatusByUserID(userId, status)

    override suspend fun insertOrUpdateUserInfoDB(user: UserData): DBResource<Unit> {
        return if (isUserDBNotEmpty()) {
            updateUserInfoDB(user)
        } else {
            insertUserInfoDB(user)
        }
    }


    override suspend fun userInfoSync(user: UserData): APIResource<List<IdInfoRemoteResponse>> {
        if (user.objectId.isNullOrEmpty()) {
            val response = remoteUserDataSource.insertUserRemote(user)
            if (response is APIResource.Success) {
                updateObjectId(response.value)
            }
            return response
        } else {
            val respone = remoteUserDataSource.updateUserRemote(user)
            return respone
        }
    }

    override suspend fun getUserObjectId(): String? {
        val response = localUserDataSource.getUser()
        val objectId = (response as? DBResource.Success)?.value?.objectId
        return if (!objectId.isNullOrBlank()) objectId else null
    }

    override suspend fun deleteUserInfo() {
        localUserDataSource.deleteUserEntity()
        localCommonDataSource.deleteAddressEntity()
        localCommonDataSource.deleteContactEntity()
    }

    override suspend fun isUserDBNotEmpty(): Boolean {
        val response = getUserInfoDB()
        if (response is DBResource.Success) {
            return response.value.userId > 0
        } else {
            return false
        }
    }

    override suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String
    ) = remoteCommonDataSource.uploadImage(context, uri, fileName)

    override suspend fun uploadImage(
        context: Context,
        image: Bitmap,
        fileName: String
    ) = remoteCommonDataSource.uploadBitMapImage(context, image, fileName)

    suspend fun updateObjectId(value: List<IdInfoRemoteResponse>) {
        value.forEach {
            when (it.table) {
                SyncType.USER.type -> localUserDataSource.updateUserObjectId(it.id, it.objectId)
                SyncType.CONTACT.type -> localCommonDataSource.updateContactObjectId(
                    it.id,
                    it.objectId
                )

                SyncType.ADDRESS.type -> localCommonDataSource.updateAddressObjectId(
                    it.id,
                    it.objectId

                )

                else -> {

                }

            }
        }
    }


    suspend fun getUerRemote(userId: String) = remoteUserDataSource.getUserRemote(userId)


    private fun UserInfoResponse.toEntities(): Pair<AddressEntity, ContactEntity> {
        val addressEntity = this.toAddressEntity()
        val contactEntity = this.toContactEntity()

        return Pair(addressEntity, contactEntity)


    }

    private fun getUserData(user: User, contact: Contact, address: Address) =
        UserData(
            userId = user.userId,
            businessName = user.businessName,
            logo = user.logo,
            signature = user.signature,
            objectId = user.objectId,
            syncStatus = user.status,
            address = address,
            contact = contact
        )

}


