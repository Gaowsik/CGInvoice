package com.example.cginvoice.data.source.local.dataSource.invoice

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dao.invoice.InvoiceDao
import com.example.cginvoice.data.source.local.dao.user.UserDao
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity

class LocalInvoiceDataSourceImpl(private val invoiceDao: InvoiceDao) : LocalInvoiceDataSource,
    BaseRepo() {
    override suspend fun insertInvoiceEntity(invoiceEntity: InvoiceEntity) = safeDbCall {
        invoiceDao.insertInvoiceEntity(invoiceEntity)
    }

    override suspend fun deleteInvoiceEntity() = safeDbCall {
        invoiceDao.deleteInvoiceEntity()
    }
}