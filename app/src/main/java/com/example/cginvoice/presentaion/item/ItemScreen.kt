package com.example.cginvoice.presentaion.item

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
import com.example.cginvoice.domain.model.item.ItemData
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.utills.SearchBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemScreen(
    navController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    isSelectedFromInvoice: Boolean = false
) {
    val getItems by viewModel.getItemInfo.collectAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true, block = {
        viewModel.getItems()
    })
    Column(modifier = Modifier.padding(vertical = paddingValues.calculateTopPadding())) {
        SearchBar(
            searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
        ) {
            items(getItems.size) { number ->
                ItemsItem(getItems[number], onClickListener = {
                    handleIsSelectedFromInvoice(isSelectedFromInvoice, navController, it)
                }) {
                    viewModel.deleteItem(it)
                }
            }
        }
    }
}


private fun handleIsSelectedFromInvoice(
    isSelectedFromInvoice: Boolean,
    navController: NavHostController,
    item: ItemData
) {
    if (isSelectedFromInvoice) {
        navController.previousBackStackEntry?.savedStateHandle?.set("selectedItem", item)
        navController.popBackStack()

    } else {
        navController.navigate(NavItem.AddItem.createRoute(itemId = item.itemId))
    }
}