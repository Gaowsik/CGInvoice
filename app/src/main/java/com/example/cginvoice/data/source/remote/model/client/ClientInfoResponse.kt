package com.example.cginvoice.data.source.remote.model.client

import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact

data class ClientInfoResponse(
    val clientId: Int = 0,
    val name: String,
    val userInfoObjectId: String,
    val objectId: String? = "",
    val address: Address,
    val contact: Contact
)
