package com.example.cginvoice.presentaion.invoice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.utills.IconWithLabel
import com.example.cginvoice.utills.TextInputWithLabel
import com.example.cginvoice.utills.TextWithLabel
import kotlinx.coroutines.launch


@Composable
fun AddInvoiceScreen(
    navController: NavHostController,
    viewModel: InvoiceDetailViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    invoiceId: Int,
    topBarConfig: (TopBarConfig) -> Unit
) {

    val invoiceState by viewModel.invoiceDetailState.collectAsState()


    topBarConfig(
        TopBarConfig("Add Invoice", actions = {
            TextButton(onClick = { viewModel.updateInvoiceData() }) {
                Text("Save")
            }
        })
    )

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        launch {
            savedStateHandle?.getStateFlow<Int?>("selectedClientId", null)?.collect { id ->
                id?.let { viewModel.updateInvoiceField { it.copy(clientId = id.toLong()) } }
            }
        }
        launch {
            savedStateHandle?.getStateFlow<String?>("selectedClientName", null)?.collect { name ->
                name?.let { viewModel.updateInvoiceField { it.copy(clientName = name) } }
            }
        }

        launch {
            savedStateHandle?.getStateFlow<InvoiceItemData?>("selectedInvoiceItem", null)
                ?.collect { invoiceItem ->
                    invoiceItem?.let { viewModel.addInvoiceItemToCurrentState(invoiceItem) }
                }
        }

        launch {
            savedStateHandle?.getStateFlow<Payment?>("selectedPayment", null)
                ?.collect { paymentItem ->
                    paymentItem?.let { viewModel.upsertPayment(paymentItem) }
                }
        }

    }

    Column(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TextInputWithLabel(label = "Name", value = invoiceState.name) { name ->
            viewModel.updateInvoiceField { it.copy(invoiceData = name) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextWithLabel("Client", invoiceState.clientName) {
            navController.navigate(NavItem.Client.createRoute(isSelected = true))
        }
        Spacer(modifier = Modifier.height(16.dp))
        IconWithLabel(Icons.Default.Add, "Add Items") {
            navController.navigate(NavItem.AddItem.createRoute(-1, isSelectedFromInvoice = true))
        }

        LazyColumn(
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            items(invoiceState.invoiceItemList.size) { number ->
                InvoiceItemForUIState(invoiceState.invoiceItemList[number]) {
                    viewModel.deleteInvoiceItemFromCurrentState(it)
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        IconWithLabel(Icons.Default.Add, "Add Payments") {
            navigateToAddPayment(navController, null)
        }

        LazyColumn(
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            items(invoiceState.paymentList.size) { number ->
                InvoicePaymentUIState(invoiceState.paymentList[number]) {
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextInputWithLabel("Note", invoiceState.note, modifier = Modifier.height(200.dp)) { note ->
            viewModel.updateInvoiceField { it.copy(note = note) }
        }




    }

}

fun navigateToAddPayment(
    navController: NavHostController,
    payment: Payment? = null
) {
    payment?.let {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("payment", it)
    }

    navController.navigate(NavItem.AddPayment.path)
}
