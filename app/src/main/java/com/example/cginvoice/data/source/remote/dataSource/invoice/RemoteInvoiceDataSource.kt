package com.example.cginvoice.data.source.remote.dataSource.invoice

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice

interface RemoteInvoiceDataSource {
    suspend fun insertInvoiceRemote(invoice: Invoice): APIResource<List<IdInfoRemoteResponse>>

    suspend fun updateInvoiceRemote(invoice: Invoice): APIResource<List<IdInfoRemoteResponse>>

    suspend fun getAllInvoices(userId: String): APIResource<List<Invoice>>

    suspend fun deleteInvoice(
        invoiceObjectId: String, invoiceId: Int
    ): APIResource<IdInfoRemoteResponse>

}