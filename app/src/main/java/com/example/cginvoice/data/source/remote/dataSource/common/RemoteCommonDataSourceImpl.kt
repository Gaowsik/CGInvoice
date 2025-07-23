package com.example.cginvoice.data.source.remote.dataSource.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.remote.back4AppClientManager.core.Back4AppImageHandler
import javax.inject.Inject

class RemoteCommonDataSourceImpl @Inject constructor(private val back4AppImageHandler: Back4AppImageHandler) :
    RemoteCommonDataSource, BaseRepo() {
    override suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String
    ) = back4AppImageHandler.uploadImage(context, uri, fileName)

    override suspend fun uploadBitMapImage(
        context: Context,
        image: Bitmap,
        fileName: String
    ) = back4AppImageHandler.uploadBitMapImage(context, image, fileName)


}