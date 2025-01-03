package com.example.cginvoice.presentaion

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object ClientScreen : Route
    @Serializable
    data object InvoiceScreen : Route
    @Serializable
    data object MoreScreen : Route
}