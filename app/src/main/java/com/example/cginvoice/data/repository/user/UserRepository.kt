package com.example.cginvoice.data.repository.user

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.example.cginvoice.domain.model.user.UserData

interface UserRepository {
    suspend fun insertUserInfoResponseToDB(userInfoResponse: UserInfoResponse): DBResource<Unit>
    suspend fun getUserInfoDB(): DBResource<UserData>
    suspend fun getUserInfo(): DBResource<UserData>
    suspend fun getAndSaveUserInfoRemote(): DBResource<Unit>
    suspend fun getFirstUserInfoRemote(): APIResource<UserData>
    suspend fun createUserInfo(user: User, contact: Contact, address: Address)
    suspend fun updateUserInfoDB(user: UserData): DBResource<Unit>
    suspend fun insertUserInfoDB(user: UserData): DBResource<Unit>
    suspend fun insertOrUpdateUserInfoDB(user: UserData): DBResource<Unit>
    suspend fun updateStatusByUserID(userId: Int, status: String): DBResource<Unit>
    suspend fun userInfoSync(user: UserData): APIResource<List<IdInfoRemoteResponse>>
    suspend fun deleteUserInfo()
    suspend fun isUserDBNotEmpty(): Boolean
    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String
    ): APIResource<String?>

    suspend fun uploadImage(
        context: Context,
        image: Bitmap,
        fileName: String
    ): APIResource<String?>
}