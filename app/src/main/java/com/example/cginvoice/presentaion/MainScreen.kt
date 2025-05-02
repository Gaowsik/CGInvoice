package com.example.cginvoice.presentaion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cginvoice.R
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.presentaion.nav.NavigationScreens

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarContent: @Composable (() -> Unit) = when (currentRoute) {
        NavItem.Client.path -> {
            { TopAppBar(title = { Text("Clients") }) }
        }

        NavItem.Invoice.path -> {
            { TopAppBar(title = { Text("Invoices") }) }
        }

        NavItem.More.path -> {
            { TopAppBar(title = { Text("More") }) }
        }

        NavItem.User.path -> {
            { TopAppBar(title = { Text("User") }) }

        }

        else -> {
            { TopAppBar(title = { Text("CG Invoice") }) }
        }
    }
    Scaffold( bottomBar = {
        BottomAppBar { BottomNavigationBar(navController = navController) }

    }, floatingActionButton = {
        if (currentRoute == NavItem.Client.path || currentRoute == NavItem.Invoice.path) {
            FloatingActionButton(onClick = { handleFabClick(currentRoute, navController) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add)
                )
            }
        }
    }) { padding ->
        NavigationScreens(navController = navController, paddingValues = padding)
    }


}


fun handleFabClick(currentRoute: String?, navController: NavHostController) {
    when (currentRoute) {
        NavItem.Client.path -> {
            navController.navigate(NavItem.Invoice.path) // Navigate to Add Client screen
        }

        NavItem.Invoice.path -> {
            navController.navigate(NavItem.Client.path) // Replace with your actual invoice creation route
        }

        else -> {

        }

        // Add more routes as needed
    }
}

