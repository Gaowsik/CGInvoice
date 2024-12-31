package com.example.cginvoice.domain.model.client

data class Client(
    val clientId: Int,
    val clientName: String,
    val addressId: Int,
    val contactId: Int,
    val userId: Int,
    val objectId: String,
    val status : String
)

