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
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.client.AddClientScreen
import com.example.cginvoice.presentaion.client.ClientScreen
import com.example.cginvoice.presentaion.invoice.AddInvoiceScreen
import com.example.cginvoice.presentaion.invoice.AddPaymentScreen
import com.example.cginvoice.presentaion.invoice.InvoiceListScreen
import com.example.cginvoice.presentaion.item.AddItemScreen
import com.example.cginvoice.presentaion.item.ItemScreen
import com.example.cginvoice.presentaion.user.UserDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreens(
    navController: NavHostController,
    paddingValues: PaddingValues,
    topBarConfig: (TopBarConfig) -> Unit
) {
    NavHost(navController, startDestination = NavItem.Invoice.path) {
        composable(NavItem.Invoice.path) {
            InvoiceListScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
        composable(
            route = NavItem.Client.path + "/{isSelected}",
            arguments = listOf(navArgument("isSelected") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isSelected = backStackEntry.arguments?.getBoolean("isSelected") ?: false
            ClientScreen(
                navController = navController,
                paddingValues = paddingValues,
                isSelected = isSelected
            )

        }

        composable(NavItem.Client.path) {
            ClientScreen(
                navController = navController,
                paddingValues = paddingValues,
                isSelected = false

            )
        }

        composable(
            route = NavItem.Items.path + "/{isSelectedFromInvoice}",
            arguments = listOf(navArgument("isSelectedFromInvoice") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isSelected = backStackEntry.arguments?.getBoolean("isSelectedFromInvoice") ?: false
            ItemScreen(
                navController = navController,
                paddingValues = paddingValues,
                isSelectedFromInvoice = isSelected
            )

        }

        composable(NavItem.Items.path) {
            ItemScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
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

        composable(
            route = NavItem.AddItem.path + "/{itemId}?fromInvoice={fromInvoice}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                },
                navArgument("fromInvoice") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->

            val itemId =
                backStackEntry.arguments?.getInt("itemId") ?: -1

            val isSelectedFromInvoice =
                backStackEntry.arguments?.getBoolean("fromInvoice") ?: false

            AddItemScreen(
                navController = navController,
                paddingValues = paddingValues,
                itemId = itemId,
                isSelectedFromInvoice = isSelectedFromInvoice,
                topBarConfig = topBarConfig
            )

        }

        composable(
            route = NavItem.AddPayment.path
        ) {
            val payment = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Payment>("payment")


            AddPaymentScreen(
                navController = navController,
                paddingValues = paddingValues,
                payment = payment,
                topBarConfig = topBarConfig
            )

        }




        composable(
            route = NavItem.AddInvoice.path + "/{invoiceId}",
            arguments = listOf(navArgument("invoiceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getInt("invoiceId") ?: -1
            AddInvoiceScreen(
                navController,
                paddingValues = paddingValues,
                invoiceId = invoiceId,
                topBarConfig = topBarConfig
            )
        }

    }
}