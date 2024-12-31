package com.example.cginvoice.data.source.remote.back4AppClientManager.client

import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.common.IdInfoRemoteResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.utills.SyncType
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Back4AppClientManager {
    suspend fun insertClientInfo(clientInfo: ClientInfoResponse): APIResource<List<IdInfoRemoteResponse>> =
        withContext(Dispatchers.IO)
        {
            try {
                // Save Address object
                val addressData = clientInfo.address
                val addressObject: ParseObject = ParseObject("Address").apply {
                    addressData?.let { put("country", it.country) }
                    addressData?.let { put("street", it.street) }
                    addressData?.let { put("aptSuite", it.aptSuite) }
                    addressData?.let { put("postalCode", it.postalCode) }
                    addressData?.let { put("city", it.city) }
                }

                // Save Address synchronously
                addressObject.save()

                // Save Contact object
                val contactData = clientInfo.contact
                val contactObject = ParseObject("Contact").apply {
                    contactData?.let { put("name", it.name) }
                    contactData?.let { put("phone", it.phone) }
                    contactData?.let { put("cell", it.cell) }
                    contactData?.let { put("email", it.email) }
                    contactData?.let { put("fax", it.fax) }
                    contactData?.let { put("website", it.website) }
                }

                // Save Contact synchronously
                contactObject.save()

                // Retrieve existing UserInfo object using objectId
                val userInfoObject = ParseObject("UserInfo").apply {
                    objectId = clientInfo.userInfoObjectId // Set the existing UserInfo objectId
                }

                // Create Client object and link existing UserInfo, Address, and Contact
                val clientObject = ParseObject("Client").apply {
                    put("name", clientInfo.name)
                    put("addressId", addressObject) // Link Address
                    put("contactID", contactObject) // Link Contact
                    put("userId", userInfoObject) // Link existing UserInfo
                }

                // Save Client synchronously
                clientObject.save()

                // Create a list of IdInfoResponse with provided IDs and server-generated objectIds
                val idInfoRemoteResponses = listOf(
                    IdInfoRemoteResponse(
                        id = clientInfo.clientId,
                        table = SyncType.CLIENT.type,
                        objectId = clientObject.objectId
                    ),
                    IdInfoRemoteResponse(
                        id = clientInfo.address.addressId,
                        table = SyncType.ADDRESS.type,
                        objectId = addressObject.objectId
                    ),
                    IdInfoRemoteResponse(
                        id = clientInfo.contact.contactId,
                        table = SyncType.CONTACT.type,
                        objectId = contactObject.objectId
                    )
                )

                APIResource.Success(idInfoRemoteResponses)
            } catch (e: Exception) {
                // Return an error in case of an exception
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message.toString() // No specific error body from Back4App
                )
            }
        }


    suspend fun updateClientInfo(clientInfoResponse: ClientInfoResponse): APIResource<List<IdInfoRemoteResponse>> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch and update the Address object
                val addressQuery = ParseQuery.getQuery<ParseObject>("Address")
                val addressObject = addressQuery.get(clientInfoResponse.address.objectId.toString())

                addressObject.apply {
                    clientInfoResponse.address.let {
                        put("country", it.country)
                        put("street", it.street)
                        put("aptSuite", it.aptSuite)
                        put("postalCode", it.postalCode)
                        put("city", it.city)
                    }
                }

                // Save Address object
                addressObject.save()

                // Fetch and update the Contact object
                val contactQuery = ParseQuery.getQuery<ParseObject>("Contact")
                val contactObject = contactQuery.get(clientInfoResponse.contact.objectId.toString())

                contactObject.apply {
                    clientInfoResponse.contact.let {
                        put("name", it.name)
                        put("phone", it.phone)
                        put("cell", it.cell)
                        put("email", it.email)
                        put("fax", it.fax)
                        put("website", it.website)
                    }
                }

                // Save Contact object
                contactObject.save()

                // Fetch and update the existing UserInfo object
                val userInfoObject = ParseObject("UserInfo").apply {
                    objectId =
                        clientInfoResponse.userInfoObjectId // Link to existing UserInfo object
                }

                // Fetch and update the Client object
                val clientQuery = ParseQuery.getQuery<ParseObject>("Client")
                val clientObject = clientQuery.get(clientInfoResponse.objectId.toString())

                clientObject.apply {
                    put("name", clientInfoResponse.name)
                    put("addressId", addressObject) // Link Address
                    put("contactID", contactObject) // Link Contact
                    put("userId", userInfoObject) // Link existing UserInfo
                }

                // Save Client object
                clientObject.save()

                // Create a list of IdInfoResponse with provided IDs and server-generated objectIds
                val idInfoRemoteResponses = listOf(
                    IdInfoRemoteResponse(
                        id = clientInfoResponse.clientId,
                        table = SyncType.CLIENT.type,
                        objectId = clientObject.objectId
                    ),
                    IdInfoRemoteResponse(
                        id = clientInfoResponse.address.addressId,
                        table = SyncType.ADDRESS.type,
                        objectId = addressObject.objectId
                    ),
                    IdInfoRemoteResponse(
                        id = clientInfoResponse.contact.contactId,
                        table = SyncType.CONTACT.type,
                        objectId = contactObject.objectId
                    )
                )

                // If all operations succeed, return Success with the list of IdInfoResponse
                APIResource.Success(idInfoRemoteResponses)
            } catch (e: Exception) {
                // Handle exceptions and return an Error
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message
                )
            }
        }

    suspend fun getClientInfo(clientId: String): APIResource<ClientInfoResponse> =
        withContext(Dispatchers.IO) {
            try {
                // Query to fetch the Client object by clientId
                val query = ParseQuery.getQuery<ParseObject>("Client")
                query.whereEqualTo("objectId", clientId)
                query.include("userId") // Include related UserInfo object
                query.include("addressId") // Include related Address object
                query.include("contactID") // Include related Contact object

                // Fetch the first matching Client object
                val clientObject = query.first

                // If Client object is found, map it to a ClientInfoResponse data class
                val clientInfoResponse = clientObject.let {
                    // Extract related UserInfo, Address, and Contact objects
                    val userInfoObject = it.getParseObject("userId")
                    val addressObject = it.getParseObject("addressId")
                    val contactObject = it.getParseObject("contactID")

                    // Create Address data class from the ParseObject
                    val addressData = addressObject?.let { addr ->
                        Address(
                            objectId = addr.objectId,
                            country = addr.getString("country") ?: "",
                            street = addr.getString("street") ?: "",
                            aptSuite = addr.getString("aptSuite") ?: "",
                            postalCode = addr.getString("postalCode") ?: "",
                            city = addr.getString("city") ?: ""
                        )
                    }

                    // Create Contact data class from the ParseObject
                    val contactData = contactObject?.let { contact ->
                        Contact(
                            objectId = contact.objectId,
                            name = contact.getString("name") ?: "",
                            phone = contact.getLong("phone"),
                            cell = contact.getLong("cell"),
                            email = contact.getString("email") ?: "",
                            fax = contact.getString("fax") ?: "",
                            website = contact.getString("website") ?: ""
                        )
                    }

                    // Create and return the ClientInfoResponse data class
                    ClientInfoResponse(
                        clientId = clientId.toIntOrNull() ?: 0,
                        name = it.getString("name") ?: "",
                        userInfoObjectId = userInfoObject?.objectId ?: "",
                        objectId = it.objectId ?: "",
                        address = addressData!!,
                        contact = contactData!!
                    )
                }

                // If successful, wrap in APIResource.Success
                APIResource.Success(clientInfoResponse)
            } catch (e: Exception) {
                // Handle exceptions and return an error resource
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message
                )
            }
        }

/*    suspend fun getClientsByUserId(userId: String): APIResource<List<ClientInfoResponse>> =
        withContext(Dispatchers.IO) {
            try {
                // Query to fetch all Client objects by userId
                val query = ParseQuery.getQuery<ParseObject>("Client")
                query.whereEqualTo("userId", ParseObject.createWithoutData("UserInfo", userId))
                query.include("userId") // Include related UserInfo object
                query.include("addressId") // Include related Address object
                query.include("contactID") // Include related Contact object

                // Fetch all matching Client objects
                val clientObjects = query.find()

                // Map each Client object to a ClientInfoResponse
                val clientInfoResponses = clientObjects.map { clientObject ->
                    // Extract related UserInfo, Address, and Contact objects
                    val userInfoObject = clientObject.getParseObject("userId")
                    val addressObject = clientObject.getParseObject("addressId")
                    val contactObject = clientObject.getParseObject("contactID")

                    // Create Address data class from the ParseObject
                    val addressData = addressObject?.let { addr ->
                        Address(
                            objectId = addr.objectId,
                            country = addr.getString("country") ?: "",
                            street = addr.getString("street") ?: "",
                            aptSuite = addr.getString("aptSuite") ?: "",
                            postalCode = addr.getString("postalCode") ?: "",
                            city = addr.getString("city") ?: ""
                        )
                    }

                    // Create Contact data class from the ParseObject
                    val contactData = contactObject?.let { contact ->
                        Contact(
                            objectId = contact.objectId,
                            name = contact.getString("name") ?: "",
                            phone = contact.getLong("phone"),
                            cell = contact.getLong("cell"),
                            email = contact.getString("email") ?: "",
                            fax = contact.getString("fax") ?: "",
                            website = contact.getString("website") ?: ""
                        )
                    }

                    // Create ClientInfoResponse data class
                    ClientInfoResponse(
                        clientId = clientObject.objectId.toIntOrNull() ?: 0,
                        name = clientObject.getString("name") ?: "",
                        userInfoObjectId = userInfoObject?.objectId ?: "",
                        objectId = clientObject.objectId,
                        address = addressData!!,
                        contact = contactData!!
                    )
                }

                // If successful, wrap in APIResource.Success
                APIResource.Success(clientInfoResponses)
            } catch (e: Exception) {
                // Handle exceptions and return an error resource
                APIResource.ErrorString(
                    isNetworkError = e is java.net.UnknownHostException,
                    errorCode = (e as? ParseException)?.code,
                    errorBody = e.message
                )
            }
        }*/

    suspend fun getClientsByUserId(userId: String): List<ClientInfoResponse> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val query = ParseQuery.getQuery<ParseObject>("Client")
                query.whereEqualTo("userId", ParseObject.createWithoutData("UserInfo", userId))
                query.include("userId")
                query.include("addressId")
                query.include("contactID")

                query.findInBackground { clientObjects, e ->
                    if (e != null) {
                        continuation.resumeWithException(e)
                    } else {
                        try {
                            val clientInfoResponses = clientObjects.map { clientObject ->
                                val userInfoObject = clientObject.getParseObject("userId")
                                val addressObject = clientObject.getParseObject("addressId")
                                val contactObject = clientObject.getParseObject("contactID")

                                val addressData = addressObject?.let { addr ->
                                    Address(
                                        objectId = addr.objectId,
                                        country = addr.getString("country") ?: "",
                                        street = addr.getString("street") ?: "",
                                        aptSuite = addr.getString("aptSuite") ?: "",
                                        postalCode = addr.getString("postalCode") ?: "",
                                        city = addr.getString("city") ?: ""
                                    )
                                } ?: throw IllegalArgumentException("Address data is missing")

                                val contactData = contactObject?.let { contact ->
                                    Contact(
                                        objectId = contact.objectId,
                                        name = contact.getString("name") ?: "",
                                        phone = contact.getLong("phone"),
                                        cell = contact.getLong("cell"),
                                        email = contact.getString("email") ?: "",
                                        fax = contact.getString("fax") ?: "",
                                        website = contact.getString("website") ?: ""
                                    )
                                } ?: throw IllegalArgumentException("Contact data is missing")

                                ClientInfoResponse(
                                    clientId = clientObject.objectId.toIntOrNull() ?: 0,
                                    name = clientObject.getString("name") ?: "",
                                    userInfoObjectId = userInfoObject?.objectId ?: "",
                                    objectId = clientObject.objectId,
                                    address = addressData,
                                    contact = contactData
                                )
                            }
                            continuation.resume(clientInfoResponses)
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
}