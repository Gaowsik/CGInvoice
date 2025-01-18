package com.example.cginvoice.presentaion.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cginvoice.presentaion.InvoiceScreen
import com.example.cginvoice.presentaion.MoreScreen
import com.example.cginvoice.presentaion.client.ClientScreen
import com.example.cginvoice.presentaion.user.UserDetailScreen

@Composable
fun NavigationScreens(navController: NavHostController) {
    NavHost(navController, startDestination = NavItem.Invoice.path) {
        composable(NavItem.Invoice.path) { InvoiceScreen() }
        composable(NavItem.Client.path) { ClientScreen() }
        composable(NavItem.More.path) { MoreScreen() }
        composable(NavItem.User.path) { UserDetailScreen() }
    }
}