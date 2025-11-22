package com.example.cginvoice.data.repository.invoice

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSource
import com.example.cginvoice.data.source.local.entitiy.invoice.toInvoiceEntity
import com.example.cginvoice.data.source.remote.dataSource.invoice.RemoteInvoiceDataSource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.domain.model.invoice.toPaymentEntity
import com.example.cginvoice.domain.model.invoiceItem.toInvoiceItemEntity
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val remoteInvoiceDataSource: RemoteInvoiceDataSource,
    private val localInvoiceDataSource: LocalInvoiceDataSource,
    private val userRepository: UserRepository
) : InvoiceRepository {
    override suspend fun getInvoiceList(): DBResource<List<Invoice>> {
        val response = getInvoicesFromDb()
        if (response is DBResource.Success && response.value.isNotEmpty()) {
            return response
        } else {
            val responseRemote = getAndSaveInvoiceListFromRemote()
            if (responseRemote is DBResource.Success) {
                return getInvoicesFromDb()
            }
        }
        return DBResource.Error(Exception())
    }

    override suspend fun getPaidInvoiceList(): DBResource<List<Invoice>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUnpaidInvoiceList(): DBResource<List<Invoice>> {
        TODO("Not yet implemented")
    }

    override suspend fun getInvoiceDetailByInvoiceId(invoiceId: Int): DBResource<Invoice> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdateInvoiceDB(invoice: Invoice): DBResource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(invoice: Invoice) {
        TODO("Not yet implemented")
    }

    override suspend fun syncAllInvoices(invoiceList: List<Invoice>): APIResource<List<IdInfoRemoteResponse>> {
        TODO("Not yet implemented")
    }

    private suspend fun getInvoicesFromDb() = localInvoiceDataSource.getInvoices()

    suspend fun getAndSaveInvoiceListFromRemote(): DBResource<Unit> {
        val userObjectId = userRepository.getUserObjectId()

        if (userObjectId.isNullOrBlank()) {
            return DBResource.Error(Exception("Missing user ID"))
        }
        val response = remoteInvoiceDataSource.getAllInvoices(userObjectId)
        if (response is APIResource.Success) {
            response.value.forEach {
                val responseInsert = insertInvoiceResponseToDB(it)
                if (responseInsert is DBResource.Error) {
                    return DBResource.Error(responseInsert.exception)
                }
            }
            return DBResource.Success(Unit)
        } else if (response is APIResource.Error) {
            return DBResource.Error(Exception(response.errorBody.toString()))
        }
        return DBResource.Error(Exception("Unknown error"))
    }

    private suspend fun insertInvoiceResponseToDB(invoice: Invoice): DBResource<Unit> {

        val response = localInvoiceDataSource.insertInvoiceEntity(invoice.toInvoiceEntity())

        return if (response is DBResource.Success) {
            val invoiceId = response.value// Assuming DBResource.Success returns the rowId


            invoice.invoiceItemList.forEach { item ->
                val insertItemResponse = localInvoiceDataSource.insertInvoiceItemEntity(
                    item.toInvoiceItemEntity(invoiceId)
                )
                if (insertItemResponse is DBResource.Error) {
                    return DBResource.Error(insertItemResponse.exception)
                }
            }

            invoice.paymentList.forEach { payment ->
                val insertPaymentResponse = localInvoiceDataSource.insertPaymentEntity(
                    payment.toPaymentEntity(invoiceId)
                )
                if (insertPaymentResponse is DBResource.Error) {
                    return DBResource.Error(insertPaymentResponse.exception)
                }
            }

            DBResource.Success(Unit)
        } else if (response is DBResource.Error) {
            DBResource.Error(response.exception)
        } else {
            DBResource.Error(Exception("Unknown error while inserting invoice"))
        }
    }


}