package com.example.cginvoice.presentaion.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cginvoice.presentaion.InvoiceScreen
import com.example.cginvoice.presentaion.MoreScreen
import com.example.cginvoice.presentaion.client.ClientScreen
import com.example.cginvoice.presentaion.user.UserDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreens(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController, startDestination = NavItem.Invoice.path) {
        composable(NavItem.Invoice.path) { InvoiceScreen() }
        composable(NavItem.Client.path) { ClientScreen() }
        composable(NavItem.More.path) { MoreScreen() }
        composable(NavItem.User.path) {
            UserDetailScreen(
                navController,
                paddingValues = paddingValues
            )
        }
    }
}