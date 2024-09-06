package com.example.cginvoice.data.source.remote.model

import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact

data class UserInfoResponse(
    val userId: Int = 0,
    val businessName: String,
    val logo: String,
    val signature: String,
    val objectId: String? = "",
    val address: Address,
    val contact: Contact

)
