package com.example.cginvoice.domain.model.user

import com.example.cginvoice.domain.model.common.Address
import com.example.cginvoice.domain.model.common.Contact

data class UserAndAddress(
    val user: User,
    val address: Address
)