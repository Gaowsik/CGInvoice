package com.example.cginvoice.data.source.local.dataSource.invoice

import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.source.local.entitiy.invoicItem.InvoiceItemEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.InvoiceEntity
import com.example.cginvoice.data.source.local.entitiy.invoice.PaymentEntity
import com.example.cginvoice.data.source.local.relation.invoice.InvoiceWithItemsAndPayments
import com.example.cginvoice.domain.model.invoice.Invoice
import kotlinx.coroutines.flow.Flow

interface LocalInvoiceDataSource {

    suspend fun insertInvoiceEntity(invoiceEntity: InvoiceEntity): DBResource<Long>

    suspend fun deleteInvoice(invoiceEntity: InvoiceEntity): DBResource<Unit>

    suspend fun getInvoiceWithItemsAndPayments(invoiceId: Long): DBResource<List<InvoiceWithItemsAndPayments>>

    suspend fun updateInvoiceObjectId(invoiceId: Int, newObjectId: String): DBResource<Unit>

    suspend fun getInvoices(): DBResource<List<Invoice>>

    suspend fun updateStatusByInvoiceId(invoiceId: Int, syncStatus: String): DBResource<Unit>

    suspend fun insertInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): DBResource<Long>

    suspend fun updateInvoiceItemEntity(invoiceItemEntity: InvoiceItemEntity): DBResource<Unit>

    suspend fun updateInvoiceItemObjectId(invoiceItemId: Int, newObjectId: String): DBResource<Unit>

    suspend fun deleteAllInvoiceEntity(): DBResource<Unit>

    suspend fun deleteInvoiceItem(invoiceItemEntity: InvoiceItemEntity): DBResource<Unit>

    suspend fun insertPaymentEntity(paymentEntity: PaymentEntity): DBResource<Long>

    suspend fun updatePaymentEntity(paymentEntity: PaymentEntity): DBResource<Unit>

    suspend fun updatePaymentObjectId(paymentId: Int, newObjectId: String): DBResource<Unit>

    suspend fun deleteAllPaymentEntity(): DBResource<Unit>

    suspend fun deletePaymentItem(paymentEntity: PaymentEntity): DBResource<Unit>
}