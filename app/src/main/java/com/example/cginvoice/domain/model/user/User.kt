package com.example.cginvoice.domain.model.user

data class User(
    val userId: String,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: String,
    val contactId: String,
    val taxId: String,
    val objectId : String
)
