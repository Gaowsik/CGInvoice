package com.example.cginvoice.data.source.remote.user

import android.util.Log
import com.example.cginvoice.data.APIResource
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Back4AppUserManager {
    fun insertUserInfo(user: User, addressData: Address, contactData: Contact) {

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


    fun updateAddress(addressData: Address) {
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
                        Log.e("updateAddress", "Failed to update Address: ${addressSaveException.message}")
                    }
                }
            } else {
                Log.e("updateAddress", "Failed to fetch Address: ${addressFetchException?.message}")
            }
        }
    }

    fun updateContact(contactData: Contact) {
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
                        Log.e("updateContact", "Failed to update Contact: ${contactSaveException.message}")
                    }
                }
            } else {
                Log.e("updateContact", "Failed to fetch Contact: ${contactFetchException?.message}")
            }
        }
    }


    fun updateUserInfo(user: User, addressData: Address, contactData: Contact) {
        // Fetch and update the Address object
        val addressQuery = ParseQuery.getQuery<ParseObject>("Address")
        addressQuery.getInBackground(addressData.addressId.toString()) { addressObject, addressFetchException ->
            if (addressFetchException == null && addressObject != null) {
                // Update Address fields
                addressObject.apply {
                    put("country", addressData.country)
                    put("street", addressData.street)
                    put("aptSuite", addressData.aptSuite)
                    put("postalCode", addressData.postalCode)
                    put("city", addressData.city)
                }

                // Save Address object
                addressObject.saveInBackground { addressSaveException ->
                    if (addressSaveException == null) {
                        // Fetch and update the Contact object
                        val contactQuery = ParseQuery.getQuery<ParseObject>("Contact")
                        contactQuery.getInBackground(contactData.contactId.toString()) { contactObject, contactFetchException ->
                            if (contactFetchException == null && contactObject != null) {
                                // Update Contact fields
                                contactObject.apply {
                                    put("name", contactData.name)
                                    put("phone", contactData.phone)
                                    put("cell", contactData.cell)
                                    put("email", contactData.email)
                                    put("fax", contactData.fax)
                                    put("website", contactData.website)
                                }

                                // Save Contact object
                                contactObject.saveInBackground { contactSaveException ->
                                    if (contactSaveException == null) {
                                        // Fetch and update the UserInfo object
                                        val userInfoQuery = ParseQuery.getQuery<ParseObject>("UserInfo")
                                        userInfoQuery.whereEqualTo("address", addressObject)
                                        userInfoQuery.whereEqualTo("contact", contactObject)

                                        userInfoQuery.findInBackground { userInfoList, userInfoFetchException ->
                                            if (userInfoFetchException == null && userInfoList != null && userInfoList.isNotEmpty()) {
                                                val userInfoObject = userInfoList[0]
                                                // Update UserInfo fields
                                                userInfoObject.apply {
                                                    put("businessName", user.businessName)
                                                    put("logo", user.logo)
                                                    put("signature", user.signature)
                                                    put("address", addressObject) // Link updated Address
                                                    put("contact", contactObject) // Link updated Contact
                                                }

                                                // Save UserInfo object
                                                userInfoObject.saveInBackground { userInfoSaveException ->
                                                    if (userInfoSaveException == null) {
                                                        Log.d("updateUserInfo", "UserInfo updated successfully.")
                                                    } else {
                                                        Log.e("updateUserInfo", "Failed to update UserInfo: ${userInfoSaveException.message}")
                                                    }
                                                }
                                            } else {
                                                Log.e("updateUserInfo", "Failed to fetch UserInfo: ${userInfoFetchException?.message}")
                                            }
                                        }
                                    } else {
                                        Log.e("updateUserInfo", "Failed to update Contact: ${contactSaveException.message}")
                                    }
                                }
                            } else {
                                Log.e("updateUserInfo", "Failed to fetch Contact: ${contactFetchException?.message}")
                            }
                        }
                    } else {
                        Log.e("updateUserInfo", "Failed to update Address: ${addressSaveException.message}")
                    }
                }
            } else {
                Log.e("updateUserInfo", "Failed to fetch Address: ${addressFetchException?.message}")
            }
        }
    }




}



