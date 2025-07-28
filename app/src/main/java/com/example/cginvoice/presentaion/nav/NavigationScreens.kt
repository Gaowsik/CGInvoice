package com.example.cginvoice.presentaion.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cginvoice.presentaion.InvoiceScreen
import com.example.cginvoice.presentaion.MoreScreen
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.client.AddClientScreen
import com.example.cginvoice.presentaion.client.ClientScreen
import com.example.cginvoice.presentaion.user.UserDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreens(
    navController: NavHostController,
    paddingValues: PaddingValues,
    topBarConfig: (TopBarConfig) ->Unit
) {
    NavHost(navController, startDestination = NavItem.Invoice.path) {
        composable(NavItem.Invoice.path) { InvoiceScreen() }
        composable(NavItem.Client.path) {
            ClientScreen(navController = navController, paddingValues = paddingValues)
   
        }

        composable(NavItem.More.path) { MoreScreen() }
        composable(NavItem.User.path) {
            UserDetailScreen(
                navController,
                paddingValues = paddingValues,
                topBarConfig = topBarConfig
            )
        }
        composable(
            route = NavItem.AddClient.path + "/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            AddClientScreen(
                navController,
                paddingValues = paddingValues,
                clientId = clientId,
                topBarConfig = topBarConfig
            )
        }

    }
}