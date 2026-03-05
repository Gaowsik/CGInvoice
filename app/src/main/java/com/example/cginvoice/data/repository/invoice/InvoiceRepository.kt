package com.example.cginvoice.data.repository.invoice

import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import java.io.File

interface InvoiceRepository {
    suspend fun getInvoiceList(): DBResource<List<Invoice>>
    suspend fun getPaidInvoiceList(): DBResource<List<Invoice>>
    suspend fun getUnpaidInvoiceList(): DBResource<List<Invoice>>
    suspend fun getInvoiceDetailByInvoiceId(invoiceId: Int): DBResource<Invoice>
    suspend fun insertOrUpdateInvoiceDB(invoice: Invoice): DBResource<Unit>
    suspend fun deleteInvoice(invoice: Invoice)
    suspend fun deleteInvoiceItem(invoiceItemData: InvoiceItemData)
    suspend fun deleteInvoicePayment(invoicePayment: Payment)
    suspend fun syncAllInvoices(invoiceList: List<Invoice>): APIResource<List<IdInfoRemoteResponse>>
    suspend fun generatePdf(invoice: Invoice): ByteArray

    suspend fun savePdf(
        pdfBytes: ByteArray,
        invoiceId: String
    ): DBResource<Uri>
}