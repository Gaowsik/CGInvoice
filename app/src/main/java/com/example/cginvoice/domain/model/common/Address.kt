package com.example.cginvoice.domain.model.common

data class Address(
    val addressId: Int = 0,
    val country: String,
    val street: String,
    val aptSuite: String,
    val postalCode: String,
    val city: String,
    val objectId: String
)
