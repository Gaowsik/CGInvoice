package com.example.cginvoice.presentaion.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.domain.model.item.toInvoiceItemData
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.invoice.InvoiceDetailViewModel
import com.example.cginvoice.presentaion.nav.Item
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.TextFieldWithLabel
import com.example.cginvoice.utills.TextInputWithLabel

@Composable
fun AddItemScreen(
    navController: NavHostController,
    viewModel: ItemViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    itemId: Int,
    isSelectedFromInvoice: Boolean = false,
    topBarConfig: (TopBarConfig) -> Unit
) {


    val itemState by viewModel.currentItemData.collectAsState()

    var taxText by remember {
        mutableStateOf(if (itemState.defaultTax == 0.0) "" else itemState.defaultTax.toString())
    }

    var discountText by remember {
        mutableStateOf(if (itemState.defaultDiscount == 0.0) "" else itemState.defaultDiscount.toString())
    }

    var unitPriceText by remember {
        mutableStateOf(if (itemState.defaultUnitPrice == 0.0) "" else itemState.defaultUnitPrice.toString())
    }

    var quantityText by remember {
        mutableStateOf(if (itemState.defaultQuantity == 0) "" else itemState.defaultQuantity.toString())
    }

    val shouldShowSaveDialog = remember { mutableStateOf(false) }

    val customDialogMessage = remember { mutableStateOf("") }

    topBarConfig(
        TopBarConfig("Add Item", actions = {
            TextButton(onClick = { handleIsSelected(isSelectedFromInvoice, navController, itemState.toInvoiceItemData(), viewModel) }) {
                Text("Save")
            }
        })
    )

    LaunchedEffect(key1 = viewModel.isSaved) {
        viewModel.isSaved.collect { isSaved ->
            if (isSaved) {
                customDialogMessage.value = "Data is Saved Successfully"
                shouldShowSaveDialog.value = true
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

    ) {

        TextInputWithLabel(label = "Name", value = itemState.itemName) { name ->
            viewModel.updateField { it.copy(itemName = name) }
        }

        TextInputWithLabel(
            label = "Description",
            value = itemState.description ?: ""
        ) { description ->
            viewModel.updateField { it.copy(description = description) }
        }

        Spacer(modifier = Modifier.height(40.dp))


        TextFieldWithLabel(
            "Unit Price",
            unitPriceText,
            KeyboardType.Decimal,
            "0.00"
        ) { unitPrice ->
            if (unitPrice.matches(Regex("^\\d*\\.?\\d*\$"))) {
                unitPriceText = unitPrice
                if (unitPrice.isNotEmpty() && unitPrice != ".") {
                    viewModel.updateField { it.copy(defaultUnitPrice = unitPrice.toDouble()) }
                }
            }
        }

        TextFieldWithLabel(
            "Quantity",
            quantityText,
            KeyboardType.Number,
            "0"
        ) { quantity ->
            if (quantity.matches(Regex("^\\d*\$"))) {
                quantityText = quantity
                if (quantity.isNotEmpty()) {
                    viewModel.updateField { it.copy(defaultQuantity = quantity.toInt()) }
                } else {
                    viewModel.updateField { it.copy(defaultQuantity = 0) }
                }
            }
        }

        TextFieldWithLabel(
            "Discount",
            discountText,
            KeyboardType.Decimal,
            "0.00%"
        ) { discount ->
            if (discount.matches(Regex("^\\d*\\.?\\d*\$"))) {
                discountText = discount
                if (discount.isNotEmpty() && discount != ".") {
                    viewModel.updateField { it.copy(defaultDiscount = discount.toDouble()) }
                }
            }
        }

        TextFieldWithLabel(
            "Tax", taxText,
            KeyboardType.Decimal, "0.00%"
        ) { tax ->
            if (tax.matches(Regex("^\\d*\\.?\\d*\$"))) {
                taxText = tax
                if (tax.isNotEmpty() && tax != ".") {
                    viewModel.updateField { it.copy(defaultTax = tax.toDouble()) }
                }
            }
        }

        MyAlertDialog(
            shouldShowDialog = shouldShowSaveDialog,
            errorMessage = customDialogMessage.value,
            onDismiss = {
                shouldShowSaveDialog.value = false
                navController.popBackStack()

            }
        )


    }
}

private fun handleIsSelected(
    isSelectedFromInvoice: Boolean,
    navController: NavHostController,
    item: InvoiceItemData,
    viewModel: ItemViewModel
) {
    if (isSelectedFromInvoice) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("selectedInvoiceItem", item)
        navController.popBackStack()
    }
    else{
        viewModel.updateItemDataDB()
    }
}