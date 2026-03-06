package com.example.cginvoice.data.source.export.dataSource

import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.invoice.Invoice

interface InvoiceExportDataSource {
    suspend fun generatePdf(invoice: Invoice): PdfDocument

    suspend fun createTempPdfFile(pdfBytes: ByteArray,fileName: String): DBResource<Uri>

    suspend fun savePdf(
        pdfByteArray: ByteArray,
        fileName: String
    ): DBResource<Uri>
}