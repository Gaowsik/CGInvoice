package com.example.cginvoice.domain.model.common

data class Address(
    val addressId: String,
    val country: String,
    val street: String,
    val aptSuite: String,
    val postalCode: String,
    val city: String,
    val objectId : String
)
