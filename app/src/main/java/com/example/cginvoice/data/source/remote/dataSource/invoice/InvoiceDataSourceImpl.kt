package com.example.cginvoice.data.source.remote.dataSource.invoice

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.source.remote.back4AppManager.invoice.Back4AppInvoiceManager
import com.example.cginvoice.domain.model.invoice.Invoice
import javax.inject.Inject

class InvoiceDataSourceImpl @Inject constructor(private val back4AppInvoiceManager: Back4AppInvoiceManager) :
    InvoiceDataSource, BaseRepo() {
    override suspend fun insertInvoiceRemote(invoice: Invoice) =
        back4AppInvoiceManager.insertInvoice(invoice.toInvoiceResponse())

    override suspend fun updateInvoiceRemote(invoice: Invoice) =
        back4AppInvoiceManager.updateInvoice(invoice.toInvoiceResponse())


    override suspend fun getAllInvoices(userId: String)  = safeApiCall{
       back4AppInvoiceManager.getAllInvoices(userId).map {
            it.toInvoice()
        }
    }

    override suspend fun deleteInvoice(
        invoiceObjectId: String,
        invoiceId: Int
    ) = back4AppInvoiceManager.deleteInvoice(invoiceObjectId, invoiceId)


}