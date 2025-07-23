package com.example.cginvoice.presentaion.nav

import androidx.navigation.NavHostController

fun NavHostController.navigateToScreen(navigateTo : String, popUpTo : String) {
    this.navigate(navigateTo) {
        popUpTo(popUpTo) { inclusive = true }
    }
}
