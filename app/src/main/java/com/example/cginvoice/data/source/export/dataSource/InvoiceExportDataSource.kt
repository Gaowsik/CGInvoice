package com.example.cginvoice.data.source.export.dataSource

import android.graphics.pdf.PdfDocument
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.domain.model.invoice.Invoice
import java.io.File

interface InvoiceExportDataSource {
    suspend fun generatePdf(invoice: Invoice): PdfDocument

    suspend fun savePdf(
        pdfDocument: PdfDocument,
        fileName: String
    ): DBResource<File>
}