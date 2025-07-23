package com.example.cginvoice.data.source.local.dataSource.invoice

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity

interface LocalInvoiceDataSource {
    suspend fun insertInvoiceEntity(invoiceEntity: InvoiceEntity) : DBResource<Unit>

    suspend fun deleteInvoiceEntity() : DBResource<Unit>
}