package com.example.cginvoice.utills

import com.example.cginvoice.data.source.remote.model.UserInfoResponse
import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

object Constants {
    val sampleAddress = Address(
        objectId = "lgFOYxNNqA",
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
}