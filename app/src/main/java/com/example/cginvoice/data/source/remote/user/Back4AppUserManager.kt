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
            put("objectId", addressData.addressId)
            put("country", addressData.country)
            put("street", addressData.street)
            put("aptSuite", addressData.aptSuite)
            put("postalCode", addressData.postalCode)
            put("city", addressData.city)
        }

        // Save Contact object
        val contactObject = ParseObject("Contact").apply {
            put("objectId", contactData.contactId)
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
}



