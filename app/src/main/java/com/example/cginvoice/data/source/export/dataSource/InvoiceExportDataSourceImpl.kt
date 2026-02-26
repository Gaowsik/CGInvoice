package com.example.cginvoice.data.source.export.dataSource

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.export.pdf.InvoicePdfGenerator
import com.example.cginvoice.domain.model.invoice.Invoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class InvoiceExportDataSourceImpl(
    private val pdfGenerator: InvoicePdfGenerator,
    private val context: Context
) : InvoiceExportDataSource, BaseRepo() {

    override suspend fun generatePdf(invoice: Invoice) =
        pdfGenerator.generate(invoice)


    override suspend fun savePdf(pdfDocument: PdfDocument, fileName: String): DBResource<File> =
        safeDbCall {
            val file = File(
                context.getExternalFilesDir(null),
                fileName
            )

            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(file))
            }
            pdfDocument.close()

            file

        }

}