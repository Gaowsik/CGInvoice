package com.example.cginvoice.presentaion.client

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.domain.model.client.Client
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.utills.SearchBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClientScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    isSelected: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val getClients by viewModel.getClientInfo.collectAsState(emptyList())

    LaunchedEffect(key1 = true, block = {
        viewModel.getClients()
    })

    Column(
        modifier = Modifier
            .padding(
                vertical = paddingValues.calculateTopPadding()
            )
    ) {
        SearchBar(
            searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
        ) {
            items(getClients.size) { number ->
                ClientItem(getClients[number]) {
                    handleIsSelected(isSelected, navController, it)
                }
            }
        }

    }


}

private fun handleIsSelected(
    isSelected: Boolean,
    navController: NavHostController,
    client: ClientData
) {
    if (isSelected) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("selectedClientId", client.clientId)

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("selectedClientName", client.name)
        navController.popBackStack()
    } else {
        navController.navigate(NavItem.AddClient.createRoute(clientId = client.clientId))
    }
}

