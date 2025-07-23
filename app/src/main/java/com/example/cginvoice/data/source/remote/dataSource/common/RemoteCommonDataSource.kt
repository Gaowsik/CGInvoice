package com.example.cginvoice.data.source.remote.dataSource.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.cginvoice.data.APIResource

interface RemoteCommonDataSource {

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String = "image.png"
    ): APIResource<String?>

    suspend fun uploadBitMapImage(
        context: Context,
        image: Bitmap,
        fileName: String
    ): APIResource<String?>
}