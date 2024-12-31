package com.example.cginvoice.domain.model.common

data class Contact(
    val contactId: Int = 0,
    val name: String = "",
    val phone: Long = 0L,
    val cell: Long = 0L,
    val email: String = "",
    val fax: String = "",
    val website: String = "",
    val objectId: String = ""
)
