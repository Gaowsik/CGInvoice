package com.example.cginvoice.data.source.remote.back4AppManager.item

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.item.ItemResponse
import com.example.cginvoice.utills.SyncType
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Back4AppItemManager {

    suspend fun insertItem(item: ItemResponse): APIResource<IdInfoRemoteResponse> =
        withContext(Dispatchers.IO) {
            try {
                val userObject = ParseObject("UserInfo").apply {
                    objectId = item.userObjectId
                }

                val itemObject = ParseObject("Item").apply {
                    put("itemName", item.itemName)
                    put("description", item.description ?: "")
                    put("defaultUnitPrice", item.defaultUnitPrice)
                    put("defaultTax", item.defaultTax)
                    put("defaultDiscount", item.defaultDiscount)
                    put("userObjectID", userObject) // relation to UserInfo
                }

                itemObject.save()

                val response = IdInfoRemoteResponse(
                    id = item.itemId, table = SyncType.ITEM.type, objectId = itemObject.objectId
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

    suspend fun updateItem(item: ItemResponse): APIResource<IdInfoRemoteResponse> =
        withContext(Dispatchers.IO) {
            try {
                val itemQuery = ParseQuery.getQuery<ParseObject>("Item")
                val itemObject = itemQuery.get(item.itemObjectId)

                val userObject = ParseObject("UserInfo").apply {
                    objectId = item.userObjectId
                }

                itemObject.apply {
                    put("itemName", item.itemName)
                    put("description", item.description ?: "")
                    put("defaultUnitPrice", item.defaultUnitPrice)
                    put("defaultTax", item.defaultTax)
                    put("defaultDiscount", item.defaultDiscount)
                    put("userObjectID", userObject)
                }

                itemObject.save()

                val response = IdInfoRemoteResponse(
                    id = item.itemId, table = SyncType.ITEM.type, objectId = itemObject.objectId
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

    suspend fun getAllItems(userId: String): List<ItemResponse> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val userPointer = ParseObject("UserInfo").apply {
                    objectId = userId
                }

                val query = ParseQuery.getQuery<ParseObject>("Item")
                query.whereEqualTo("userObjectID", userPointer)

                query.findInBackground { itemObjects, e ->
                    if (e != null) {
                        continuation.resumeWithException(e)
                    } else {
                        try {
                            val itemResponses = itemObjects.map { itemObject ->
                                ItemResponse(
                                    itemId = 0, // or parse if you're storing this separately
                                    itemObjectId = itemObject.objectId,
                                    itemName = itemObject.getString("itemName") ?: "",
                                    description = itemObject.getString("description"),
                                    defaultUnitPrice = itemObject.getDouble("defaultUnitPrice"),
                                    defaultTax = itemObject.getDouble("defaultTax"),
                                    defaultDiscount = itemObject.getDouble("defaultDiscount"),
                                    userObjectId = itemObject.getParseObject("userObjectID")?.objectId
                                        ?: ""
                                )
                            }
                            continuation.resume(itemResponses)
                        } catch (ex: Exception) {
                            continuation.resumeWithException(ex)
                        }
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    suspend fun deleteItem(itemObjectId: String): APIResource<Unit> = withContext(Dispatchers.IO) {
        try {
            val itemQuery = ParseQuery.getQuery<ParseObject>("Item")
            val itemObject = itemQuery.get(itemObjectId)

            itemObject.delete()

            APIResource.Success(Unit)
        } catch (e: Exception) {
            APIResource.ErrorString(
                isNetworkError = e is java.net.UnknownHostException,
                errorCode = (e as? ParseException)?.code,
                errorBody = e.message.toString()
            )
        }
    }

}