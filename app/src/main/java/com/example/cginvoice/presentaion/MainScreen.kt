package com.example.cginvoice.presentaion

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.cginvoice.R
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.presentaion.nav.NavigationScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(bottomBar = {
        BottomAppBar { BottomNavigationBar(navController = navController) }

    }, floatingActionButton = {
        FloatingActionButton(onClick = {navController.navigate(NavItem.User.path)}) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
        }
    }) { NavigationScreens(navController = navController) }
}