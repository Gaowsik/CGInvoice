package com.example.cginvoice.domain.model.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val addressId: Int = 0,
    val country: String = "",
    val street: String = "",
    val aptSuite: String = "",
    val postalCode: String = "",
    val city: String = "",
    val objectId: String? = ""
) : Parcelable
