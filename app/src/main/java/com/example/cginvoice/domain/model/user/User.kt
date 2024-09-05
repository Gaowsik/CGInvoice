package com.example.cginvoice.domain.model.user

data class User(
    val userId: Int = 0,
    val businessName: String,
    val logo: String,
    val signature: String,
    val addressId: Int,
    val contactId: Int,
    val objectId: String
)
