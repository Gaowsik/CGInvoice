package com.example.cginvoice.data.source.remote.back4AppManager.core

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.cginvoice.data.APIResource
import com.parse.ParseException
import com.parse.ParseFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Back4AppImageHandler {

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String = "image.png"
    ): APIResource<String?> =
        withContext(Dispatchers.IO)
        {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                val file = ParseFile(fileName, bytes)
                file.save()
                if (file.url.isNotEmpty()) {
                    APIResource.Success(file.url)// return the URL
                } else {
                    APIResource.Success(null)// return the URL
                }

            } catch (e: Exception) {
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message.toString() // No specific error body from Back4App
                )
            }
        }

    suspend fun uploadBitMapImage(
        context: Context,
        image: Bitmap,  // Accept Bitmap directly
        fileName: String = "signature.png"
    ): APIResource<String?> =
        withContext(Dispatchers.IO)
        {
            try {
                // Step 1: Save the Bitmap to a file
                val filePath = File(context.cacheDir, fileName)
                val fileOutputStream = FileOutputStream(filePath)
                image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()

                // Step 2: Convert the file to a byte array
                val fileBytes = filePath.readBytes()

                // Step 3: Create a ParseFile and upload it
                val file = ParseFile(fileName, fileBytes)
                file.save()

                // Step 4: Return the URL of the uploaded file
                if (file.url.isNotEmpty()) {
                    APIResource.Success(file.url)  // return the URL
                } else {
                    APIResource.Success(null)  // return null if no URL
                }

            } catch (e: Exception) {
                // Handle the error
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message.toString()  // No specific error body from Back4App
                )
            }
        }

}