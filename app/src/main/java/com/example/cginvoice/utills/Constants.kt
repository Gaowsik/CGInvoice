package com.example.cginvoice.utills

import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact
import com.example.cginvoice.domain.model.user.User

object Constants {
    val sampleAddress = Address(
        addressId = "12345L",
        country = "USA",
        street = "456 Elm St",
        aptSuite = "Suite 12",
        postalCode = "78901",
        city = "Maskeliya"
    )

    val sampleContact = Contact(
        contactId = "fSA3X2zEts",
        name = "Jane Smooth",
        phone = 9876543210L,
        cell = 1234567890L,
        email = "janesmith@example.com",
        fax = "555-555-5555",
        website = "https://janesmith.com"
    )

    val sampleUser = User(
        userId = "54321L",
        businessName = "Sample Business",
        logo = "https://example.com/logo.png",
        signature = "https://example.com/signature.png",
        addressId = sampleAddress.addressId,
        contactId = sampleContact.contactId,
        taxId = "TAX123456789"
    )
}