package com.example.cginvoice.data.source.remote.user

import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Back4AppUserManager {
    suspend fun insertUserInfo(user: User, addressData: Address, contactData: Contact) =
        withContext(Dispatchers.IO) {

            // Save Address object
            val addressObject: ParseObject = ParseObject("Address").apply {
                put("country", addressData.country)
                put("street", addressData.street)
                put("aptSuite", addressData.aptSuite)
                put("postalCode", addressData.postalCode)
                put("city", addressData.city)
            }

            // Save Contact object
            val contactObject = ParseObject("Contact").apply {
                put("name", contactData.name)
                put("phone", contactData.phone)
                put("cell", contactData.cell)
                put("email", contactData.email)
                put("fax", contactData.fax)
                put("website", contactData.website)
            }

            // Save both objects asynchronously
            addressObject.saveInBackground { addressSaveException ->
                if (addressSaveException == null) {
                    // Address saved successfully
                    contactObject.saveInBackground { contactSaveException ->
                        if (contactSaveException == null) {
                            // Contact saved successfully
                            // Create UserInfo object and link address and contact
                            val userInfoObject = ParseObject("UserInfo").apply {
                                put("businessName", user.businessName)
                                put("logo", user.logo)
                                put("signature", user.signature)
                                put("address", addressObject) // Link Address
                                put("contact", contactObject) // Link Contact
                            }


                            // Save UserInfo object
                            userInfoObject.saveInBackground { userInfoSaveException ->
                                if (userInfoSaveException == null) {
                                    Log.d("insertUserInfo", "UserInfo saved successfully.")
                                } else {
                                    Log.e(
                                        "insertUserInfo",
                                        "Failed to save UserInfo: ${userInfoSaveException.message}"
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "insertUserInfo",
                                "Failed to save Contact: ${contactSaveException.message}"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "insertUserInfo",
                        "Failed to save Address: ${addressSaveException.message}"
                    )
                }
            }
        }


    suspend fun insertUserInfoR(userInfoResponse: UserInfoResponse) =
        withContext(Dispatchers.IO) {

            // Save Address object
            var addressData = userInfoResponse.address
            val addressObject: ParseObject = ParseObject("Address").apply {
                addressData?.let { put("country", it.country) }
                addressData?.let { put("street", it.street) }
                addressData?.let { put("aptSuite", it.aptSuite) }
                addressData?.let { put("postalCode", it.postalCode) }
                addressData?.let { put("city", it.city) }
            }

            // Save Contact object
            var contactData = userInfoResponse.contact
            val contactObject = ParseObject("Contact").apply {
                contactData?.let { put("name", it.name) }
                contactData?.let { put("phone", it.phone) }
                contactData?.let { put("cell", it.cell) }
                contactData?.let { put("email", it.email) }
                contactData?.let { put("fax", it.fax) }
                contactData?.let { put("website", it.website) }
            }

            // Save both objects asynchronously
            addressObject.saveInBackground { addressSaveException ->
                if (addressSaveException == null) {
                    // Address saved successfully
                    contactObject.saveInBackground { contactSaveException ->
                        if (contactSaveException == null) {
                            // Contact saved successfully
                            // Create UserInfo object and link address and contact
                            val userInfoObject = ParseObject("UserInfo").apply {
                                put("businessName", userInfoResponse.businessName)
                                put("logo", userInfoResponse.logo)
                                put("signature", userInfoResponse.signature)
                                put("address", addressObject) // Link Address
                                put("contact", contactObject) // Link Contact
                            }


                            // Save UserInfo object
                            userInfoObject.saveInBackground { userInfoSaveException ->
                                if (userInfoSaveException == null) {
                                    Log.d("insertUserInfo", "UserInfo saved successfully.")
                                } else {
                                    Log.e(
                                        "insertUserInfo",
                                        "Failed to save UserInfo: ${userInfoSaveException.message}"
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "insertUserInfo",
                                "Failed to save Contact: ${contactSaveException.message}"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "insertUserInfo",
                        "Failed to save Address: ${addressSaveException.message}"
                    )
                }
            }
        }


    suspend fun updateAddress(addressData: Address) = withContext(Dispatchers.IO) {
        val addressQuery = ParseQuery.getQuery<ParseObject>("Address")
        addressQuery.getInBackground(addressData.addressId.toString()) { addressObject, addressFetchException ->
            if (addressFetchException == null && addressObject != null) {
                addressObject.apply {
                    put("country", addressData.country)
                    put("street", addressData.street)
                    put("aptSuite", addressData.aptSuite)
                    put("postalCode", addressData.postalCode)
                    put("city", addressData.city)
                }

                addressObject.saveInBackground { addressSaveException ->
                    if (addressSaveException == null) {
                        Log.d("updateAddress", "Address updated successfully.")
                    } else {
                        Log.e(
                            "updateAddress",
                            "Failed to update Address: ${addressSaveException.message}"
                        )
                    }
                }
            } else {
                Log.e("updateAddress", "Failed to fetch Address: ${addressFetchException?.message}")
            }
        }
    }

    suspend fun updateContact(contactData: Contact) = withContext(Dispatchers.IO) {
        val contactQuery = ParseQuery.getQuery<ParseObject>("Contact")
        contactQuery.getInBackground(contactData.contactId.toString()) { contactObject, contactFetchException ->
            if (contactFetchException == null && contactObject != null) {
                contactObject.apply {
                    put("name", contactData.name)
                    put("phone", contactData.phone)
                    put("cell", contactData.cell)
                    put("email", contactData.email)
                    put("fax", contactData.fax)
                    put("website", contactData.website)
                }

                contactObject.saveInBackground { contactSaveException ->
                    if (contactSaveException == null) {
                        Log.d("updateContact", "Contact updated successfully.")
                    } else {
                        Log.e(
                            "updateContact",
                            "Failed to update Contact: ${contactSaveException.message}"
                        )
                    }
                }
            } else {
                Log.e("updateContact", "Failed to fetch Contact: ${contactFetchException?.message}")
            }
        }
    }

    suspend fun updateUserInfo(userInfoResponse: UserInfoResponse) = withContext(Dispatchers.IO) {
        val userInfoQuery = ParseQuery.getQuery<ParseObject>("UserInfo")
        userInfoQuery.getInBackground(userInfoResponse.userId.toString()) { userInfoObject, userInfoFetchException ->
            if (userInfoFetchException == null && userInfoObject != null) {
                userInfoObject.apply {
                    put("businessName", userInfoResponse.businessName)
                    put("logo", userInfoResponse.logo)
                    put("signature", userInfoResponse.signature)
                }

                userInfoObject.saveInBackground { contactSaveException ->
                    if (contactSaveException == null) {
                        Log.d("updateUserInfo", "UserInfo updated successfully.")
                    } else {
                        Log.e(
                            "updateUserInfo",
                            "Failed to update UserInfo: ${contactSaveException.message}"
                        )
                    }
                }
            } else {
                Log.e(
                    "updateUserInfo",
                    "Failed to fetch UserInfo: ${userInfoFetchException?.message}"
                )
            }
        }
    }


    suspend fun updateUserInfoR(userinfoResponse: UserInfoResponse) =
        withContext(Dispatchers.IO) {
            // Fetch and update the Address object
            val addressQuery = ParseQuery.getQuery<ParseObject>("Address")
            addressQuery.getInBackground(userinfoResponse.address?.addressId.toString()) { addressObject, addressFetchException ->
                if (addressFetchException == null && addressObject != null) {
                    // Update Address fields
                    addressObject.apply {
                        userinfoResponse.address?.let { put("country", it.country) }
                        userinfoResponse.address?.let { put("street", it.street) }
                        userinfoResponse.address?.let { put("aptSuite", it.aptSuite) }
                        userinfoResponse.address?.let { put("postalCode", it.postalCode) }
                        userinfoResponse.address?.city?.let { put("city", it) }
                    }

                    // Save Address object
                    addressObject.saveInBackground { addressSaveException ->
                        if (addressSaveException == null) {
                            // Fetch and update the Contact object
                            val contactQuery = ParseQuery.getQuery<ParseObject>("Contact")
                            contactQuery.getInBackground(userinfoResponse.contact?.contactId.toString()) { contactObject, contactFetchException ->
                                if (contactFetchException == null && contactObject != null) {
                                    // Update Contact fields
                                    contactObject.apply {
                                        userinfoResponse.contact?.let { put("name", it.name) }
                                        userinfoResponse.contact?.let { put("phone", it.phone) }
                                        userinfoResponse.contact?.let { put("cell", it.cell) }
                                        userinfoResponse.contact?.let { put("email", it.email) }
                                        userinfoResponse.contact?.let { put("fax", it.fax) }
                                        userinfoResponse.contact?.let { put("website", it.website) }
                                    }

                                    // Save Contact object
                                    contactObject.saveInBackground { contactSaveException ->
                                        if (contactSaveException == null) {
                                            // Fetch and update the UserInfo object
                                            val userInfoQuery =
                                                ParseQuery.getQuery<ParseObject>("UserInfo")
                                            userInfoQuery.getInBackground(userinfoResponse.userId.toString()) { userInfoObject, userInfoFetchException ->
                                                if (userInfoFetchException == null && userInfoObject != null) {
                                                    userInfoObject.apply {
                                                        put(
                                                            "businessName",
                                                            userinfoResponse.businessName
                                                        )
                                                        put("logo", userinfoResponse.logo)
                                                        put("signature", userinfoResponse.signature)
                                                        put(
                                                            "address",
                                                            addressObject
                                                        ) // Link Address
                                                        put("contact", contactObject)
                                                    }

                                                    // Save UserInfo object
                                                    userInfoObject.saveInBackground { userInfoSaveException ->
                                                        if (userInfoSaveException == null) {
                                                            Log.d(
                                                                "updateUserInfo",
                                                                "UserInfo updated successfully."
                                                            )
                                                        } else {
                                                            Log.e(
                                                                "updateUserInfo",
                                                                "Failed to update UserInfo: ${userInfoSaveException.message}"
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Log.e(
                                                        "updateUserInfo",
                                                        "Failed to fetch UserInfo: ${userInfoFetchException?.message}"
                                                    )
                                                }
                                            }
                                        } else {
                                            Log.e(
                                                "updateUserInfo",
                                                "Failed to update Contact: ${contactSaveException.message}"
                                            )
                                        }
                                    }
                                } else {
                                    Log.e(
                                        "updateUserInfo",
                                        "Failed to fetch Contact: ${contactFetchException?.message}"
                                    )
                                }
                            }
                        } else {
                            Log.e(
                                "updateUserInfo",
                                "Failed to update Address: ${addressSaveException.message}"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "updateUserInfo",
                        "Failed to fetch Address: ${addressFetchException?.message}"
                    )
                }
            }
        }

    suspend fun getUserInfo(userId: String): UserInfoResponse = withContext(Dispatchers.IO) {
        // Query to fetch the UserInfo object by userId
        val query = ParseQuery.getQuery<ParseObject>("UserInfo")
        query.whereEqualTo("objectId", userId)
        query.include("address") // Include related Address object
        query.include("contact") // Include related Contact object

        val userInfoObject = query.first // Fetch the first matching UserInfo object

        // If UserInfo object is found, map it to a UserInfo data class
        userInfoObject.let {
            // Extract Address and Contact objects
            val addressObject = it.getParseObject("address")
            val contactObject = it.getParseObject("contact")

            // Create Address data class from the ParseObject
            val addressData = addressObject?.let { addr ->
                Address(
                    addressId = addr.objectId,
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
                    contactId = contact.objectId,
                    name = contact.getString("name") ?: "",
                    phone = contact.getLong("phone"),
                    cell = contact.getLong("cell"),
                    email = contact.getString("email") ?: "",
                    fax = contact.getString("fax") ?: "",
                    website = contact.getString("website") ?: ""
                )
            }

            // Create and return the UserInfo data class
            UserInfoResponse(
                userId = it.objectId,
                businessName = it.getString("businessName") ?: "",
                logo = it.getString("logo") ?: "",
                signature = it.getString("signature") ?: "",
                address = addressData,
                contact = contactData
            )
        }
    }
}
