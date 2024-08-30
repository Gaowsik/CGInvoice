package com.example.cginvoice.domain.model.common

data class Address(
    val addressId: Long,
    val country: String,
    val street: String,
    val aptSuite: String,
    val postalCode: Long,
    val city: String
)
