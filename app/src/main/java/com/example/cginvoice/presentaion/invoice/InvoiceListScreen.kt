package com.example.cginvoice.presentaion.invoice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.SearchBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceListScreen(
    navController: NavHostController,
    viewModel: InvoiceViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    val getInvoices by viewModel.getInvoices.collectAsState(emptyList())
    val loadingState by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val shouldShowDialog = remember { mutableStateOf(false) }
    val customDialogMessage = remember { mutableStateOf("") }

    LaunchedEffect(key1 = true, block = {
        viewModel.getInvoices()
    })

    LaunchedEffect(key1 = viewModel.errorMessage) {
        viewModel.errorMessage.collect { error ->
            if (error.isNotEmpty()) {
                customDialogMessage.value = error
                shouldShowDialog.value = true
            }
        }
    }
    Column(modifier = Modifier.padding(vertical = paddingValues.calculateTopPadding())) {
        SearchBar(
            searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
        ) {
            items(getInvoices.size) { number ->
                InvoiceItem(getInvoices[number], onClickListener = {
                    navController.navigate(NavItem.AddClient.createRoute(clientId = it))
                }) {
                    viewModel.deleteInvoice(it)
                }
            }
        }
    }

    MyAlertDialog(
        shouldShowDialog = shouldShowDialog, errorMessage = customDialogMessage.value, onDismiss = {
            shouldShowDialog.value = false

        })


    if (loadingState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {}, contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}