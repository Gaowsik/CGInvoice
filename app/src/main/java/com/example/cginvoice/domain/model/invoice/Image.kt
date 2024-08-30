package com.example.cginvoice.domain.model.invoice

data class Image(
    val imageId: Long,
    val imageURL: String,
    val invoiceId: Long
)
