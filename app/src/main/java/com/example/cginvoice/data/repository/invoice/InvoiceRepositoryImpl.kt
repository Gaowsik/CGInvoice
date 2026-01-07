package com.example.cginvoice.data.repository.invoice

import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.DBResource
import com.example.cginvoice.data.repository.user.UserRepository
import com.example.cginvoice.data.source.local.dataSource.invoice.LocalInvoiceDataSource
import com.example.cginvoice.data.source.local.entitiy.invoice.toInvoiceEntity
import com.example.cginvoice.data.source.remote.dataSource.invoice.RemoteInvoiceDataSource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.domain.model.invoice.Invoice
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoice.toPaymentEntity
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.domain.model.invoiceItem.toInvoiceItemEntity
import com.example.cginvoice.utills.SyncStatus
import com.example.cginvoice.utills.SyncType
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
        return when (val response = localInvoiceDataSource.getInvoicesListWithItemsAndPayments()) {

            is DBResource.Success -> {
                val paidInvoices = response.value.filter { invoice ->
                    val totalPaid = invoice.paymentList.sumOf { it.amount }
                    totalPaid >= invoice.totalAmount
                }

                DBResource.Success(paidInvoices)
            }

            is DBResource.Error -> DBResource.Error(response.exception)

            else -> DBResource.Error(Exception())
        }
    }

    override suspend fun getUnpaidInvoiceList(): DBResource<List<Invoice>> {
        return when (val response = localInvoiceDataSource.getInvoicesListWithItemsAndPayments()) {

            is DBResource.Success -> {
                val paidInvoices = response.value.filter { invoice ->
                    val totalPaid = invoice.paymentList.sumOf { it.amount }
                    totalPaid < invoice.totalAmount
                }

                DBResource.Success(paidInvoices)
            }

            is DBResource.Error -> DBResource.Error(response.exception)

            else -> DBResource.Error(Exception())
        }
    }

    override suspend fun getInvoiceDetailByInvoiceId(invoiceId: Int) =
        localInvoiceDataSource.getInvoiceWithItemsAndPayments(invoiceId.toLong())


    override suspend fun insertOrUpdateInvoiceDB(invoice: Invoice): DBResource<Unit> {
        return if (invoice.invoiceId.toInt() != 0) {
            updateInvoiceResponseToDB(invoice)
        } else {
            insertInvoiceResponseToDB(invoice)
        }
    }

    override suspend fun deleteInvoice(invoice: Invoice) {
        if (invoice.invoiceObjectId.isNullOrEmpty()) {
            localInvoiceDataSource.deleteInvoice(invoice.toInvoiceEntity())
        } else {
            updateStatusDelete(invoice.invoiceId.toInt())
        }
    }

    override suspend fun deleteInvoiceItem(invoiceItemData: InvoiceItemData) {
        if (invoiceItemData.invoiceItemObjectId.isNullOrEmpty()) {
            localInvoiceDataSource.deleteInvoiceItem(invoiceItemData.toInvoiceItemEntity())
        } else {
            updateStatusDeleteInvoiceItem(invoiceItemData.invoiceItemId)
        }
    }

    override suspend fun deleteInvoicePayment(invoicePayment: Payment) {
        if (invoicePayment.paymentObjectId.isNullOrEmpty()) {
            localInvoiceDataSource.deletePaymentItem(invoicePayment.toPaymentEntity())
        } else {
            updateStatusDeletePaymentItem(invoicePayment.paymentId!!)
        }
    }


    override suspend fun syncAllInvoices(invoiceList: List<Invoice>): APIResource<List<IdInfoRemoteResponse>> {
        val idInfoRemoteResponseList = emptyList<IdInfoRemoteResponse>().toMutableList()
        invoiceList.filter { it.syncStatus != SyncStatus.COMPLETED.status }.forEach { invoice ->
            val response = invoiceSync(invoice)

            when (response) {
                is APIResource.Success -> {
                    Log.d("suc", "")
                    idInfoRemoteResponseList.addAll(response.value)
                }

                is APIResource.Error -> {
                    Log.d("Error", response.errorBody.toString())

                }

                APIResource.Loading -> {}
                is APIResource.ErrorString -> {
                    Log.d("suc", "")
                }
            }
        }
        return APIResource.Success(idInfoRemoteResponseList)
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
            val invoiceId = response.value


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


    private suspend fun updateInvoiceResponseToDB(invoice: Invoice): DBResource<Unit> {

        val response = localInvoiceDataSource.updateInvoiceEntity(invoice.toInvoiceEntity())

        return if (response is DBResource.Success) {
            val invoiceId = response.value// Assuming DBResource.Success returns the rowId


            invoice.invoiceItemList.forEach { item ->
                val insertItemResponse = localInvoiceDataSource.insertInvoiceItemEntity(
                    item.toInvoiceItemEntity(invoiceId.toLong())
                )
                if (insertItemResponse is DBResource.Error) {
                    return DBResource.Error(insertItemResponse.exception)
                }
            }

            invoice.paymentList.forEach { payment ->
                val insertPaymentResponse = localInvoiceDataSource.insertPaymentEntity(
                    payment.toPaymentEntity(invoiceId.toLong())
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


    private suspend fun invoiceInfoSync(invoice: Invoice): APIResource<List<IdInfoRemoteResponse>> {
        return when (invoice.syncStatus) {
            SyncStatus.PENDING.status -> {
                if (invoice.invoiceObjectId.isEmpty()) {
                    val response = remoteInvoiceDataSource.insertInvoiceRemote(invoice)
                    if (response is APIResource.Success) {
                        response.value.forEach { mapping ->
                            when (mapping.table) {
                                SyncType.INVOICE_ITEM.type -> {
                                    updateInvoiceItemObjectId(mapping)
                                }

                                SyncType.PAYMENT.type -> {
                                    updatePaymentObjectId(mapping)
                                }

                                SyncType.INVOICE.type -> {
                                    updateInvoiceObjectId(mapping)
                                }
                            }
                        }
                    }
                    response
                } else {
                    val updateResponse = remoteInvoiceDataSource.updateInvoiceRemote(invoice)
                    if (updateResponse is APIResource.Success) {
                        updateResponse.value.forEach { mapping ->
                            when (mapping.table) {
                                SyncType.INVOICE_ITEM.type -> {
                                    updateInvoiceItemObjectId(mapping)
                                }

                                SyncType.PAYMENT.type -> {
                                    updatePaymentObjectId(mapping)
                                }

                                SyncType.INVOICE.type -> {
                                    updateInvoiceObjectId(mapping)
                                }
                            }


                        }


                    }

                    updateResponse
                }
            }

            SyncStatus.DELETE.status -> {
                if (!invoice.invoiceObjectId.isNullOrEmpty()) {
                    val response = remoteInvoiceDataSource.deleteInvoice(
                        invoiceObjectId = invoice.invoiceObjectId,
                        invoiceId = invoice.invoiceId.toInt()
                    )
                    if (response is APIResource.Success) {
                        localInvoiceDataSource.deleteInvoice(invoice.toInvoiceEntity())
                    }
                    return APIResource.Success(emptyList())
                } else {
                    // Decide what to return if there's nothing to delete
                    APIResource.ErrorString(
                        false, null, "Unsupported sync status: ${invoice.syncStatus}"
                    )
                }
            }

            else -> {
                // Handle unknown status explicitly
                APIResource.ErrorString(
                    false, null, "Unsupported sync status: ${invoice.syncStatus}"
                )
            }
        }
    }

    private suspend fun updateInvoiceObjectId(it: IdInfoRemoteResponse) {
        localInvoiceDataSource.updateInvoiceObjectId(
            it.id, it.objectId
        )
        localInvoiceDataSource.updateStatusByInvoiceId(it.id, SyncStatus.COMPLETED.status)

    }


    private suspend fun updateInvoiceItemObjectId(it: IdInfoRemoteResponse) {
        localInvoiceDataSource.updateInvoiceItemObjectId(
            it.id, it.objectId
        )

    }

    private suspend fun updatePaymentObjectId(it: IdInfoRemoteResponse) {
        localInvoiceDataSource.updatePaymentObjectId(
            it.id, it.objectId
        )

    }

    private suspend fun updateStatusDelete(invoiceId: Int) {
        localInvoiceDataSource.updateStatusByInvoiceId(invoiceId, SyncStatus.DELETE.status)
    }

    private suspend fun updateStatusDeleteInvoiceItem(invoiceItemId: Int) {
        localInvoiceDataSource.updateStatusByInvoiceId(invoiceItemId, SyncStatus.DELETE.status)
    }

    private suspend fun updateStatusDeletePaymentItem(invoicePaymentId: Int) {
        localInvoiceDataSource.updateStatusByInvoiceId(invoicePaymentId, SyncStatus.DELETE.status)
    }


    private suspend fun invoiceSync(invoice: Invoice): APIResource<List<IdInfoRemoteResponse>> {
        return when (invoice.syncStatus) {
            SyncStatus.PENDING.status -> {
                if (invoice.invoiceObjectId.isNullOrEmpty()) {
                    val response = remoteInvoiceDataSource.insertInvoiceRemote(invoice)
                    if (response is APIResource.Success) {
                        updateInvoiceRelatedObjectId(response.value)
                    }
                    response
                } else {
                    remoteInvoiceDataSource.updateInvoiceRemote(invoice)
                }
            }

            SyncStatus.DELETE.status -> {
                if (!invoice.invoiceObjectId.isNullOrEmpty()) {

                    val response = remoteInvoiceDataSource.deleteInvoice(
                        invoice.invoiceObjectId, invoice.invoiceId.toInt()
                    )

                    return if (response is APIResource.Success) {
                        localInvoiceDataSource.deleteInvoice(response.value.id)
                        APIResource.Success(listOf(response.value))

                    } else {
                        APIResource.ErrorString(
                            true, null, "Something Went Wrong"
                        )
                    }
                } else {
                    return APIResource.ErrorString(
                        false, null, "Unsupported sync status: ${invoice.syncStatus}"
                    )
                }
            }

            else -> {
                APIResource.ErrorString(
                    false, null, "Unsupported sync status: ${invoice.syncStatus}"
                )
            }
        }
    }

    private suspend fun updateInvoiceRelatedObjectId(value: List<IdInfoRemoteResponse>) {
        value.forEach {
            when (it.table) {
                SyncType.INVOICE_ITEM.type -> {
                    updateInvoiceItemObjectId(it)
                }

                SyncType.INVOICE.type -> {
                    updateInvoiceObjectId(it)
                }

                SyncType.PAYMENT.type -> {
                    updatePaymentObjectId(it)
                }


            }

        }

    }


}