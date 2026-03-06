package com.example.cginvoice.data.repository.invoice

import android.net.Uri
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData

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

    suspend fun createTempPdfFile(pdfBytes: ByteArray,invoice: Invoice?): DBResource<Uri>
}