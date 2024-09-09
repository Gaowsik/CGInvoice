package com.example.cginvoice.utills

import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

object Constants {

    const val KEY_SYNC_DATA_REQUEST = "key_sync_data_request"
    const val KEY_SYNC_TYPE = "key_sync_type"

    val sampleAddress = Address(
        objectId = "",
        country = "USA",
        street = "456 Elm St",
        aptSuite = "Suite 12",
        postalCode = "78901",
        city = "Maskeliya boo"
    )

    val sampleContact = Contact(
        objectId = "mARTUCkKGu",
        name = "Jane Smooth boo",
        phone = 9876543210,
        cell = 1234567890,
        email = "janesmith@example.com",
        fax = "555-555-5555",
        website = "https://janesmith.com"
    )

    val sampleUser = User(
        objectId = "BWuEIYNAbk",
        businessName = "Sample Business boo",
        logo = "https://example.com/logo.png",
        signature = "https://example.com/signature.png",
        addressId = sampleAddress.addressId,
        contactId = sampleContact.contactId,
    )

    val sampleUserInfoResponse = UserInfoResponse(
        userId = 57678687,
        businessName = "Hello boo",
        logo = "https://example.com/logo.png",
        signature = "https://example.com/signature.png",
        address =  sampleAddress,
        contact = sampleContact
    )

    val sampleUserResponse = UserInfoResponse(
        userId = 123,
        businessName = "Acme Corpa sync",
        logo = "https://example.com/logo.png",
        signature = "John Doe",
        objectId = "",
        address = Address(
            addressId = 1,
            country = "United Stata sync",
            street = "123 Main Street",
            aptSuite = "Suite 100",
            postalCode = "12345",
            city = "New York",
            objectId = ""
        ),
        contact = Contact(
            contactId = 1,
            name = "John Doa sync",
            phone = 1234567890,
            cell = 9876543210,
            email = "john.doe@example.com",
            fax = "123-456-7891",
            website = "https://example.com",
            objectId = ""
        )
    )
}