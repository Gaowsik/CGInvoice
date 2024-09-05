package com.example.cginvoice.domain.model.user

data class User(
    val userId: Int = 0,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: String,
    val contactId: String,
    val taxId: String,
    val objectId: String
)
