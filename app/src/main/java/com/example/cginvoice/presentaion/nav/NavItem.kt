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

    object More :
        Item(path = NavPath.MORE.toString(), title = NavTitle.MORE, icon = Icons.Default.List)

    object User :
        Item(path = NavPath.USER.toString(), title = NavTitle.USER, icon = Icons.Default.Face)
}