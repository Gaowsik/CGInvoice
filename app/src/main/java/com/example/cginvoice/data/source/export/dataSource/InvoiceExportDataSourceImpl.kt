package com.example.cginvoice.data.source.export.dataSource

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.export.pdf.InvoicePdfGenerator
import com.example.cginvoice.domain.model.invoice.Invoice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class InvoiceExportDataSourceImpl @Inject constructor(
    private val pdfGenerator: InvoicePdfGenerator,
    @ApplicationContext private val context: Context
) : InvoiceExportDataSource, BaseRepo() {

    override suspend fun generatePdf(invoice: Invoice) =
        pdfGenerator.generate(invoice)

    override suspend fun createTempPdfFile(pdfBytes: ByteArray,fileName: String): DBResource<Uri> = safeDbCall {

        val file = File(
            context.cacheDir,
            fileName
        )

        FileOutputStream(file).use {
            it.write(pdfBytes)
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun savePdf(pdfByteArray: ByteArray, fileName: String): DBResource<Uri> =
        safeDbCall {
            withContext(Dispatchers.IO) {

                val resolver = context.contentResolver

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw Exception("Failed to create file")

                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(pdfByteArray)
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                uri
            }

        }

}