package com.example.cginvoice.domain.model.user

data class User(
    val userId: Long,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: Long,
    val contactId: Long,
    val taxId: String
)
