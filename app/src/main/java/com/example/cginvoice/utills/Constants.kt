package com.example.cginvoice.utills

import com.example.cginvoice.data.source.remote.model.client.ClientInfoResponse
import com.example.cginvoice.data.source.remote.model.user.UserInfoResponse
import com.example.cginvoice.domain.model.client.ClientData
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
        objectId = "",
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
        businessName = "Hello new",
        logo = "https://example.com/logo.png",
        signature = "https://example.com/signature.png",
        address =  sampleAddress,
        contact = sampleContact
    )

    val sampleUserResponse = UserInfoResponse(
        businessName = "Acme Corpa new",
        logo = "https://example.com/logo.png",
        signature = "John Doe",
        objectId = "",
        address = Address(
            country = "United Stata sync",
            street = "123 Main Street",
            aptSuite = "Suite 100",
            postalCode = "12345",
            city = "New York",
            objectId = ""
        ),
        contact = Contact(
            name = "John Doa new",
            phone = 1234567890,
            cell = 9876543210,
            email = "john.doe@example.com",
            fax = "123-456-7891",
            website = "https://example.com",
            objectId = ""
        )
    )


    val sampleClientInfoResponse = ClientInfoResponse(
        clientId = 101,
        name = "hello coporation",
        userInfoObjectId = "bRiGW9mMIn",
        objectId = "",
        address = Address(
            addressId = 201,
            country = "Sri edited",
            street = "123 Elm Street",
            aptSuite = "Apt 4B",
            postalCode = "90210",
            city = "Beverly Hills",
            objectId = ""
        ),
        contact = Contact(
            contactId = 301,
            name = "Gaowsik",
            phone = 1234567890L,
            cell = 9876543210L,
            email = "john.doe@example.com",
            fax = "123-456-7890",
            website = "https://acme-corp.com",
            objectId = ""
        )
    )

    fun getSampleClientData(): List<ClientData> {
        return listOf(
            ClientData(
                clientId = 1,
                name = "Client One",
                address = Address(
                    addressId = 101,
                    country = "USA",
                    street = "123 Main St",
                    aptSuite = "Apt 4B",
                    postalCode = "10001",
                    city = "New York",
                    objectId = ""
                ),
                contact = Contact(
                    contactId = 201,
                    name = "John Doe",
                    phone = 1234567890L,
                    cell = 9876543210L,
                    email = "john.doe@example.com",
                    fax = "123-456-789",
                    website = "https://clientone.com",
                    objectId = ""
                ),
                syncStatus = SyncStatus.PENDING.status
            ),
            ClientData(
                clientId = 2,
                name = "Client Two",
                address = Address(
                    addressId = 102,
                    country = "Canada",
                    street = "456 Maple St",
                    aptSuite = "Suite 200",
                    postalCode = "A1B 2C3",
                    city = "Toronto",
                    objectId = ""
                ),
                contact = Contact(
                    contactId = 202,
                    name = "Jane Smith",
                    phone = 1122334455L,
                    cell = 9988776655L,
                    email = "jane.smith@example.ca",
                    fax = "987-654-321",
                    website = "https://clienttwo.ca",
                    objectId = ""
                ),
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }
}
