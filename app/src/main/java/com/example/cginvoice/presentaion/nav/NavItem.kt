package com.example.cginvoice.presentaion.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import com.example.cginvoice.utills.NavTitle

sealed class NavItem {
    object Invoice :
        Item(path = NavPath.INVOICE.toString(), title = NavTitle.INVOICE, icon = Icons.Default.Home)

    object Client :
        Item(path = NavPath.CLIENT.toString(), title = NavTitle.CLIENT, icon = Icons.Default.Search)

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
        fun createRoute(itemId: Int) = NavPath.ADD_ITEM.toString() + "/$itemId"
    }

    object Items :
        Item(path = NavPath.ITEM.toString(), title = NavTitle.ITEM, icon = Icons.Default.List)

    object User :
        Item(path = NavPath.USER.toString(), title = NavTitle.USER, icon = Icons.Default.Face)
}