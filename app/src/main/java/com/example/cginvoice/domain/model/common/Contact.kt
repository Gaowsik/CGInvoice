package com.example.cginvoice.domain.model.common

data class Contact(
    val contactId: Long,
    val name: String,
    val phone: Long,
    val cell: Long,
    val email: String,
    val fax: String,
    val website: String
)
