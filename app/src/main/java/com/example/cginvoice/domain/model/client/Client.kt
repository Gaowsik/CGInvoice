package com.example.cginvoice.domain.model.client

data class Client(
    val clientId: Long,
    val clientName: String,
    val addressId: Long,
    val contactId: Long,
    val userId: Long
)
