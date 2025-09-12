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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val topBarConfig = remember { mutableStateOf(TopBarConfig(title = "CG Invoice")) }

    LaunchedEffect(currentRoute) {
        // Default config
        topBarConfig.value = when (currentRoute) {
            NavItem.Client.path -> TopBarConfig("Clients")
            NavItem.Invoice.path -> TopBarConfig("Invoices")
            NavItem.Items.path -> TopBarConfig("Items")
            NavItem.User.path -> TopBarConfig("User")
            else -> TopBarConfig("CG Invoice")
        }
    }
    Scaffold(
        topBar = { TopAppBar(
            title = { Text(topBarConfig.value.title) },
            actions = topBarConfig.value.actions
        )},
        bottomBar = {
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
        NavigationScreens(navController = navController, paddingValues = padding,topBarConfig = {
                config -> topBarConfig.value = config
        })
    }


}


fun handleFabClick(currentRoute: String?, navController: NavHostController) {
    when (currentRoute) {
        NavItem.Client.path -> {
            navController.navigate(NavItem.AddClient.createRoute(-1)) // Navigate to Add Client screen
        }

        NavItem.Invoice.path -> {
            navController.navigate(NavItem.AddClient.path) // Replace with your actual invoice creation route
        }

        NavItem.Items.path->{
            navController.navigate(NavItem.AddItem.path)
        }

        else -> {
        }

        // Add more routes as needed
    }
}

