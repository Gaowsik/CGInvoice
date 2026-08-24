package com.example.cginvoice.presentaion.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import com.example.cginvoice.utills.NavTitle
import kotlin.toString

sealed class NavItem {
    object Invoice :
        Item(path = NavPath.INVOICE.toString(), title = NavTitle.INVOICE, icon = Icons.Default.Home)

    object InvoiceList :
        Item(
            path = NavPath.INVOICE_LIST.toString(),
            title = NavTitle.INVOICE,  // Reuse same title
            icon = Icons.Default.Home  // Reuse same icon
        )

    object Client :
        Item(
            path = NavPath.CLIENT.toString(),
            title = NavTitle.CLIENT,
            icon = Icons.Default.Search
        ) {
        fun createRoute(isSelected: Boolean) = NavPath.CLIENT.toString() + "/$isSelected"
    }

    object AddClient :
        Item(
            path = NavPath.ADD_CLIENT.toString(),
            title = NavTitle.ADD_CLIENT,
            icon = Icons.Default.Search
        ) {
        fun createRoute(clientId: Int) = NavPath.ADD_CLIENT.toString() + "/$clientId"
    }

    object AddItem :
        Item(
            path = NavPath.ADD_ITEM.toString(),
            title = NavTitle.ADD_ITEM,
            icon = Icons.Default.Search
        ) {

        fun createRoute(
            itemId: Int,
            isSelectedFromInvoice: Boolean = false
        ): String {
            return "${path}/$itemId?fromInvoice=$isSelectedFromInvoice"
        }

    }

    object AddPayment :
        Item(
            path = NavPath.ADD_PAYMENT.toString(),
            title = NavTitle.ADD_PAYMENT,
            icon = Icons.Default.Search
        ){}


    object AddInvoice :
        Item(
            path = NavPath.ADD_INVOICE.toString(),
            title = NavTitle.ADD_INVOICE,
            icon = Icons.Default.Search
        ) {
        fun createRoute(invoiceId: Int) = NavPath.ADD_INVOICE.toString() + "/$invoiceId"
    }

    object Items :
        Item(path = NavPath.ITEM.toString(), title = NavTitle.ITEM, icon = Icons.Default.List){
        fun createRoute(isSelectedFromInvoice: Boolean) = NavPath.ITEM.toString() + "/$isSelectedFromInvoice"
        }

    object User :
        Item(path = NavPath.USER.toString(), title = NavTitle.USER, icon = Icons.Default.Face)
}