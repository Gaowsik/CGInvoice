package com.example.cginvoice.presentaion.invoice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cginvoice.domain.model.invoice.Payment
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.utills.CustomDatePickerDialog
import com.example.cginvoice.utills.DecimalTextField
import com.example.cginvoice.utills.TextWithLabel
import com.example.cginvoice.utills.formatDate
import com.example.cginvoice.utills.todayMillis


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPaymentScreen(
    navController: NavHostController,
    paddingValues: PaddingValues = PaddingValues(),
    payment: Payment?,
    topBarConfig: (TopBarConfig) -> Unit
) {

    val isEdit = payment != null


    var isDatePickerOpen by rememberSaveable { mutableStateOf(false) }

    var selectedDate by rememberSaveable { mutableStateOf(payment?.paymentDate ?: todayMillis()) }
    var unitPrice by rememberSaveable {
        mutableDoubleStateOf(payment?.amount ?: 0.0)
    }

    var paymentMethod by rememberSaveable {
        mutableStateOf(
            if (payment?.paymentMethod.equals(
                    PaymentMethod.Cash.name
                )
            ) PaymentMethod.Cash else PaymentMethod.Card
        )
    }

    topBarConfig(
        TopBarConfig("Add Payment", actions = {
            TextButton(onClick = {
                navigateBackWithPaymentData(
                    navController,
                    genertePayment(
                        if (isEdit) payment?.paymentId else null,
                        selectedDate,
                        unitPrice,
                        paymentMethod,
                        ""
                    )
                )
            }) {
                Text(if (isEdit) "Update" else "Add")
            }
        })
    )


    Column(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        TextWithLabel("Date", formatDate(selectedDate), imageVector = Icons.Default.DateRange) {
            isDatePickerOpen = true
        }

        DecimalTextField(
            label = "Unit Price", value = unitPrice.toString()
        ) { newPrice ->
            newPrice?.let { unitPrice = newPrice }
        }
    }

    Row {
        PaymentMethod.entries.forEach { method ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = paymentMethod == method, onClick = { paymentMethod = method })
                Text(text = method.name)
            }
        }
    }


    if (isDatePickerOpen) {
        CustomDatePickerDialog(
            selectedDateMillis = selectedDate,
            onDismiss = { isDatePickerOpen = false },
            onConfirm = {
                selectedDate = it
                isDatePickerOpen = false
            })
    }


}

fun genertePayment(
    paymentId: Int?,
    paymentDate: Long,
    amount: Double,
    paymentMethod: PaymentMethod,
    note: String
): Payment {
    return Payment(
        paymentId = paymentId,
        paymentDate = paymentDate,
        amount = amount,
        paymentMethod = paymentMethod.name,
        note = note
    )
}

fun navigateBackWithPaymentData(navController: NavHostController, payment: Payment) {

    navController.previousBackStackEntry?.savedStateHandle?.set("selectedPayment", payment)
    navController.popBackStack()
}


enum class PaymentMethod {
    Cash, Card, Online
}



