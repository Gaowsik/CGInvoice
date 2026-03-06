package com.example.cginvoice.presentaion.invoice

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.domain.model.invoiceItem.InvoiceItemData
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.utills.IconWithLabel
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.TextFieldWithLabel
import com.example.cginvoice.utills.TextInputWithLabel
import com.example.cginvoice.utills.TextWithLabel
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun AddInvoiceScreen(
    navController: NavHostController,
    viewModel: InvoiceDetailViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    invoiceId: Int,
    topBarConfig: (TopBarConfig) -> Unit
) {
    val appContext = LocalContext.current
    val invoiceState by viewModel.invoiceDetailState.collectAsState()
    val customDialogMessage = remember { mutableStateOf("") }
    val shouldShowSaveDialog = remember { mutableStateOf(false) }
    val totalPrice by viewModel.totalAmount.collectAsState()
    val pdfBytes by viewModel.generatedPdf.collectAsState(initial = null)
    val sharedUri by viewModel.shareUri.collectAsState(initial = null)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    var totalPriceText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(totalPrice) {
        totalPriceText =
            if (totalPrice == 0.0) "" else totalPrice.toString()
    }


    LaunchedEffect(key1 = true, block = {
        viewModel.loadInvoiceOnce(invoiceId)
    })

    topBarConfig(
        TopBarConfig("Add Invoice", actions = {
            TextButton(onClick = { // TODO: show generated invoice
                viewModel.onGenerateInvoiceClicked()
            }) {
                Text("Generate")
            }
            TextButton(onClick = { viewModel.updateInvoiceData() }) {
                Text("Save")
            }
        })
    )

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {

        launch {
            savedStateHandle
                ?.getStateFlow<ClientData?>("selectedClient", null)
                ?.collect { id ->
                    id?.let {
                        viewModel.updateInvoiceField { invoice ->
                            invoice.copy(
                                clientId = it.clientId.toLong(),
                                clientName = it.name,
                                clientObjectId = it.objectId ?: ""
                            )
                        }
                        savedStateHandle["selectedClientId"] = null
                    }
                }
        }



        launch {
            savedStateHandle
                ?.getStateFlow<InvoiceItemData?>("selectedInvoiceItem", null)
                ?.collect { invoiceItem ->
                    invoiceItem?.let {
                        viewModel.addInvoiceItemToCurrentState(it)
                        savedStateHandle["selectedInvoiceItem"] = null
                    }
                }
        }

        launch {
            savedStateHandle
                ?.getStateFlow<Payment?>("selectedPayment", null)
                ?.collect { payment ->
                    payment?.let {
                        viewModel.upsertPayment(it)
                        savedStateHandle["selectedPayment"] = null
                    }
                }
        }

    }

    LaunchedEffect(key1 = viewModel.isSaved) {
        viewModel.isSaved.collect { isSaved ->
            if (isSaved) {
                customDialogMessage.value = "Data is Saved Successfully"
                shouldShowSaveDialog.value = true
            }
        }
    }

    LaunchedEffect(key1 = viewModel.isSavedSuccessfull) {
        viewModel.isSavedSuccessfull.collect { isSaved ->
            if (isSaved) {
                customDialogMessage.value = "Data is Saved Successfully"
                shouldShowSaveDialog.value = true
            }
        }
    }

    LaunchedEffect(sharedUri) {
        sharedUri?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            appContext.startActivity(
                Intent.createChooser(shareIntent, "Share Invoice")
            )
        }

    }

    LaunchedEffect(pdfBytes) {
        pdfBytes?.let { bytes ->

            val file = File(appContext.cacheDir, "preview.pdf")
            file.writeBytes(bytes)

            val fileDescriptor = ParcelFileDescriptor.open(
                file,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            val renderer = PdfRenderer(fileDescriptor)
            val page = renderer.openPage(0)

            val bmp = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close()
            renderer.close()

            bitmap = bmp   // ✅ THIS triggers recomposition
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Invoice Preview"
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldWithLabel(
                    label = "Total Price",
                    value = totalPriceText,
                    keyboardType = KeyboardType.Decimal,
                    placeholder = "0.00",
                    modifier = Modifier.weight(1f)
                ) { totalPrice ->
                    if (totalPrice.matches(Regex("^\\d*\\.?\\d*$"))) {
                        totalPriceText = totalPrice
                        if (totalPrice.isNotBlank() && totalPrice != ".") {
                            viewModel.updateManualTotalAmount(totalPrice)
                        }
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.clearManualTotalAmount()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh, // or Refresh / Calculate
                        contentDescription = "Generate from items"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            IconWithLabel(Icons.Default.Add, "Add Items") {
                navController.navigate(
                    NavItem.AddItem.createRoute(
                        -1,
                        isSelectedFromInvoice = true
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 300.dp)
            ) {
                items(invoiceState.invoiceItemList.size) { number ->
                    InvoiceItemForUIState(invoiceState.invoiceItemList[number]) {
                        viewModel.deleteInvoiceItemFromCurrentState(it.itemName, it.invoiceItemId)
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
                        viewModel.deletePaymentItemFromCurrentState(
                            it.paymentDate.toLong(),
                            it.invoicePaymentId,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextInputWithLabel(
                "Note",
                invoiceState.note,
                modifier = Modifier.height(200.dp)
            ) { note ->
                viewModel.updateInvoiceField { it.copy(note = note) }
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

        bitmap?.let { bmp ->

            Dialog(
                onDismissRequest = { bitmap = null },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Invoice Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    Row(modifier = Modifier.align(Alignment.TopEnd)) {
                        TextButton(onClick = {
                            viewModel.getShareUri()
                        }, modifier = Modifier.padding(end = 4.dp)) {
                            Text("Share")
                        }



                        IconButton(
                            onClick = { bitmap = null },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Black
                            )
                        }

                    }




                    TextButton(onClick = {
                        viewModel.onDownloadInvoiceSaveClicked()
                    }, modifier = Modifier.align(Alignment.BottomEnd)) {
                        Text("Save")
                    }

                }
            }
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

