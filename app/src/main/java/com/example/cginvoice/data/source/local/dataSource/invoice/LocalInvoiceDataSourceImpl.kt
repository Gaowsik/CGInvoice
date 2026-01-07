package com.example.cginvoice.data.source.local.dataSource.invoice

import com.example.cginvoice.data.BaseRepo
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.dao.invoice.InvoiceDao
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
import com.example.cginvoice.domain.model.invoice.Invoice

class LocalInvoiceDataSourceImpl(private val invoiceDao: InvoiceDao) : LocalInvoiceDataSource,
    BaseRepo() {
    override suspend fun insertInvoiceEntity(invoiceEntity: InvoiceEntity): DBResource<Long> =
        safeDbCall {
            invoiceDao.insertInvoiceEntity(invoiceEntity)
        }

    override suspend fun updateInvoiceEntity(invoiceEntity: InvoiceEntity): DBResource<Int> =
        safeDbCall {
            invoiceDao.updateInvoiceEntity(invoiceEntity)
        }


    override suspend fun deleteInvoice(invoiceEntity: InvoiceEntity) = safeDbCall {
        invoiceDao.deleteInvoice(invoiceEntity)
    }

    override suspend fun deleteInvoice(invoiceId: Int) = safeDbCall {
        invoiceDao.deleteInvoiceByInvoiceId(invoiceId)
    }

    override suspend fun getInvoiceWithItemsAndPayments(invoiceId: Long): DBResource<Invoice> =
        safeDbCall {
            invoiceDao.getInvoiceWithItemsAndPayments(invoiceId).toInvoice()
        }

    override suspend fun updateInvoiceObjectId(
        invoiceId: Int,
        newObjectId: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updateInvoiceObjectId(invoiceId, newObjectId)
    }

    override suspend fun getInvoices(): DBResource<List<Invoice>> = safeDbCall {
        invoiceDao.getInvoices().map {
            it.toInvoice()
        }
    }

    override suspend fun getInvoicesListWithItemsAndPayments(): DBResource<List<Invoice>> =
        safeDbCall {
            invoiceDao.getInvoicesListWithItemsAndPayments().map {
                it.toInvoice()
            }
        }

    override suspend fun updateStatusByInvoiceId(
        invoiceId: Int,
        syncStatus: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updateStatusByInvoiceId(invoiceId, syncStatus)
    }

    override suspend fun updateStatusByInvoiceItemId(
        invoiceItemId: Int,
        syncStatus: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updateStatusByInvoiceItemId(invoiceItemId, syncStatus)
    }

    override suspend fun updateStatusByInvoicePaymentId(
        invoicePaymentId: Int,
        syncStatus: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updateStatusByInvoicePaymentId(invoicePaymentId, syncStatus)
    }

    override suspend fun insertInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): DBResource<Long> =
        safeDbCall {
            invoiceDao.insertInvoiceItemEntity(invoiceItemEntity)
        }

    override suspend fun updateInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): DBResource<Unit> =
        safeDbCall {
            invoiceDao.updateInvoiceItemEntity(invoiceItemEntity)
        }

    override suspend fun updateInvoiceItemObjectId(
        invoiceItemId: Int,
        newObjectId: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updateInvoiceItemObjectId(invoiceItemId, newObjectId)
    }

    override suspend fun deleteAllInvoiceEntity(): DBResource<Unit> = safeDbCall {
        invoiceDao.deleteAllInvoiceEntity()
    }

    override suspend fun deleteInvoiceItem(invoiceItemEntity: InvoiceItemEntity): DBResource<Unit> =
        safeDbCall {
            invoiceDao.deleteInvoiceItem(invoiceItemEntity)
        }

    override suspend fun insertPaymentEntity(paymentEntity: PaymentEntity): DBResource<Long> =
        safeDbCall {
            invoiceDao.insertPaymentEntity(paymentEntity)
        }

    override suspend fun updatePaymentEntity(paymentEntity: PaymentEntity): DBResource<Unit> =
        safeDbCall {
            invoiceDao.updatePaymentEntity(paymentEntity)
        }

    override suspend fun updatePaymentObjectId(
        paymentId: Int,
        newObjectId: String
    ): DBResource<Unit> = safeDbCall {
        invoiceDao.updatePaymentObjectId(paymentId, newObjectId)
    }

    override suspend fun deleteAllPaymentEntity(): DBResource<Unit> = safeDbCall {
        invoiceDao.deleteAllPaymentEntity()
    }

    override suspend fun deletePaymentItem(paymentEntity: PaymentEntity): DBResource<Unit> =
        safeDbCall {
            invoiceDao.deletePaymentItem(paymentEntity)
        }


}