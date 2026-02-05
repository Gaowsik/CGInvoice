package com.example.cginvoice.data.source.remote.back4AppManager.invoice


import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.Invoice.InvoiceItemResponse
import com.example.cginvoice.data.source.remote.model.Invoice.InvoiceResponse
import com.example.cginvoice.data.source.remote.model.Invoice.PaymentResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.utills.SyncType
import com.google.gson.Gson
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Back4AppInvoiceManager {

    suspend fun insertInvoice(invoice: InvoiceResponse): APIResource<List<IdInfoRemoteResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val userPointer = ParseObject("UserInfo").apply {
                    objectId = invoice.userObjectId.toString()
                }

                val clientPointer = ParseObject("Client").apply {
                    objectId = invoice.clientObjectId.toString()
                }

                val invoiceObj = ParseObject("Invoice").apply {
                    put("invoiceData", invoice.invoiceData)
                    put("dueDate", invoice.dueDate)
                    put("paymentStatus",invoice.paymentStatus)
                    put("totalAmount", invoice.totalAmount.toString())
                    put("note", invoice.note)
                    put("userId", userPointer)
                    put("clientId", clientPointer)
                }

                invoiceObj.save()

                val invoiceObjectId = invoiceObj.objectId

                val idMappings = mutableListOf<IdInfoRemoteResponse>()

                invoice.invoiceItemList.forEach { item ->
                    val itemObj = ParseObject("InvoiceItem").apply {
                        put("itemName", item.itemName ?: "")
                        put("description", item.description ?: "")
                        put("quantity", item.quantity)
                        put("defaultUnitPrice", item.defaultUnitPrice)
                        put("defaultTax", item.defaultTax ?: 0.0)
                        put("defaultDiscount", item.defaultDiscount ?: "")
                        put("invoiceId", invoiceObj) // pointer
                    }

                    itemObj.save()

                    idMappings.add(
                        IdInfoRemoteResponse(
                            id = item.invoiceItemId,
                            table = SyncType.INVOICE_ITEM.type,
                            objectId = itemObj.objectId
                        )
                    )
                }

                invoice.paymentList.forEach { payment ->
                    val paymentObj = ParseObject("Payment").apply {
                        put("paymentDate", payment.paymentDate)
                        put("amount", payment.amount)
                        put("paymentMethod", payment.paymentMethod ?: "")
                        put("note", payment.note ?: "")
                        put("invoiceId", invoiceObj) // pointer
                    }

                    paymentObj.save()

                    idMappings.add(
                        IdInfoRemoteResponse(
                            id = payment.paymentId!!,
                            table = SyncType.PAYMENT.type,
                            objectId = paymentObj.objectId
                        )
                    )
                }

                // -----------------------------
                // Add Invoice mapping
                // -----------------------------
                idMappings.add(
                    IdInfoRemoteResponse(
                        id = invoice.invoiceId,
                        table = SyncType.INVOICE.type,
                        objectId = invoiceObjectId
                    )
                )

                APIResource.Success(idMappings)

            } catch (e: Exception) {
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message
                )
            }
        }

    suspend fun updateInvoice(invoice: InvoiceResponse): APIResource<List<IdInfoRemoteResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val invoiceQuery = ParseQuery.getQuery<ParseObject>("Invoice")
                val invoiceObj = invoiceQuery.get(invoice.invoiceObjectId)

                // Update Invoice fields
                invoiceObj.apply {
                    put("invoiceData", invoice.invoiceData)
                    put("paymentStatus",invoice.paymentStatus)
                    put("dueDate", invoice.dueDate)
                    put("totalAmount", invoice.totalAmount.toString())
                    put("note", invoice.note ?: "") // safe null handling
                }

                invoiceObj.save()

                val idMappings = mutableListOf<IdInfoRemoteResponse>()

                // -----------------------------
                // Update Invoice Items
                // -----------------------------
                invoice.invoiceItemList.forEach { item ->
                    val itemObj = if (item.invoiceItemObjectId == null) {
                        ParseObject("InvoiceItem")
                    } else {
                        ParseQuery.getQuery<ParseObject>("InvoiceItem")
                            .get(item.invoiceItemObjectId)
                    }

                    itemObj.apply {
                        put("itemName", item.itemName ?: "")
                        put("description", item.description ?: "")
                        put("quantity", item.quantity)
                        put("defaultUnitPrice", item.defaultUnitPrice)
                        put("defaultTax", item.defaultTax ?: 0.0)
                        put("defaultDiscount", item.defaultDiscount ?: 0.0)
                        put("invoiceId", invoiceObj) // pointer
                    }

                    itemObj.save()

                    if (item.invoiceItemObjectId == null) {
                        idMappings.add(
                            IdInfoRemoteResponse(
                                id = item.invoiceItemId,
                                table = SyncType.INVOICE_ITEM.type,
                                objectId = itemObj.objectId
                            )
                        )
                    }
                }


                invoice.paymentList.forEach { payment ->
                    val paymentObj = if (payment.paymentObjectId == null) {
                        ParseObject("Payment")
                    } else {
                        ParseQuery.getQuery<ParseObject>("Payment").get(payment.paymentObjectId)
                    }

                    paymentObj.apply {
                        put("paymentDate", payment.paymentDate)
                        put("amount", payment.amount)
                        put("paymentMethod", payment.paymentMethod ?: "")
                        put("note", payment.note ?: "")
                        put("invoiceId", invoiceObj) // pointer
                    }

                    paymentObj.save()
                    if (payment.paymentObjectId == null) {
                        idMappings.add(
                            IdInfoRemoteResponse(
                                id = payment.paymentId ?: 0,
                                table = SyncType.PAYMENT.type,
                                objectId = paymentObj.objectId
                            )
                        )
                    }
                }

                // -----------------------------
                // Add Invoice mapping
                // -----------------------------
                idMappings.add(
                    IdInfoRemoteResponse(
                        id = invoice.invoiceId,
                        table = SyncType.INVOICE.type,
                        objectId = invoice.invoiceObjectId
                    )
                )

                APIResource.Success(idMappings)

            } catch (e: Exception) {
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message
                )
            }
        }

    suspend fun getAllInvoices(userId: String): List<InvoiceResponse> =
        suspendCancellableCoroutine { continuation ->

            val userPointer = ParseObject("UserInfo").apply {
                objectId = userId
            }

            val query = ParseQuery.getQuery<ParseObject>("Invoice")
            query.whereEqualTo("userId", userPointer)
            query.include("userId")
            query.include("clientId")

            query.findInBackground { invoices, e ->

                if (e != null) {
                    continuation.resumeWithException(e)
                    return@findInBackground
                }

                try {
                    val result = invoices.map { invObj ->
                        val userInfoObject = invObj.getParseObject("userId")
                        val clientObject = invObj.getParseObject("clientId")

                        // Fetch items
                        val itemQuery = ParseQuery.getQuery<ParseObject>("InvoiceItem")
                        itemQuery.whereEqualTo("invoiceId", invObj)
                        val itemListObj = itemQuery.find()

                        val itemList = itemListObj.map { item ->
                            InvoiceItemResponse(
                                invoiceItemObjectId = item.objectId,
                                itemName = item.getString("itemName"),
                                description = item.getString("description"),
                                quantity = item.getInt("quantity"),
                                defaultUnitPrice = item.getDouble("defaultUnitPrice"),
                                defaultTax = item.getDouble("defaultTax"),
                                defaultDiscount = item.getDouble("defaultDiscount"),
                                invoiceObjectId = invObj.objectId
                            )
                        }

                        // Fetch payments
                        val payQuery = ParseQuery.getQuery<ParseObject>("Payment")
                        payQuery.whereEqualTo("invoiceId", invObj)
                        val paymentObj = payQuery.find()

                        val paymentList = paymentObj.map { pay ->
                            PaymentResponse(
                                paymentObjectId = pay.objectId,
                                invoiceObjectId = invObj.objectId,
                                paymentDate = pay.getString("paymentDate") ?: "",
                                amount = pay.getDouble("amount"),
                                paymentMethod = pay.getString("paymentMethod"),
                                note = pay.getString("note")
                            )
                        }

                        InvoiceResponse(
                            invoiceData = invObj.getString("invoiceData") ?: "",
                            dueDate = invObj.getString("dueDate") ?: "",
                            invoiceObjectId = invObj.objectId,
                            totalAmount = invObj.getString("totalAmount")?:"",
                            userObjectId = userInfoObject?.objectId,
                            clientObjectId = clientObject?.objectId,
                            note = invObj.getString("note") ?: "",
                            imageId = "",
                            paymentStatus = invObj.getBoolean("paymentStatus"),
                            paymentList = paymentList,
                            invoiceItemList = itemList
                        )
                    }

                    continuation.resume(result)

                } catch (ex: Exception) {
                    continuation.resumeWithException(ex)
                }
            }
        }

    suspend fun deleteInvoice(
        invoiceObjectId: String,
        invoiceId: Int
    ): APIResource<IdInfoRemoteResponse> = withContext(Dispatchers.IO) {
        try {

            val invoiceQuery = ParseQuery.getQuery<ParseObject>("Invoice")
            val invoiceObject = invoiceQuery.get(invoiceObjectId)


            val itemQuery = ParseQuery.getQuery<ParseObject>("InvoiceItem")
            itemQuery.whereEqualTo("invoiceId", invoiceObject)
            val itemList = itemQuery.find()
            itemList.forEach { it.delete() }


            val paymentQuery = ParseQuery.getQuery<ParseObject>("Payment")
            paymentQuery.whereEqualTo("invoiceId", invoiceObject)
            val paymentList = paymentQuery.find()
            paymentList.forEach { it.delete() }


            invoiceObject.delete()

            val response = IdInfoRemoteResponse(
                id = invoiceId,
                table = SyncType.INVOICE.type,
                objectId = invoiceObject.objectId
            )

            APIResource.Success(response)

        } catch (e: Exception) {
            APIResource.ErrorString(
                isNetworkError = e is java.net.UnknownHostException,
                errorCode = (e as? ParseException)?.code,
                errorBody = e.message.toString()
            )
        }
    }

}
