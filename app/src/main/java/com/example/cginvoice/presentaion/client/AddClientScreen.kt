package com.example.cginvoice.presentaion.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.presentaion.user.SectionTitle
import com.example.cginvoice.utills.IconWithLabel
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.TextFieldWithLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(
    navController: NavHostController = NavHostController(context = LocalContext.current),
    viewModel: ClientViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    clientId: Int
) {
    val clientDetailState by viewModel.clientDetailState.collectAsState()
    val loadingState by viewModel.isLoading.collectAsState()
    val shouldShowDialog = remember { mutableStateOf(false) }
    val customDialogMessage = remember { mutableStateOf("") }

    LaunchedEffect(key1 = true, block = {
        viewModel.getClientByClientId(clientId)
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Edit Screen") },
                actions = {
                    TextButton(onClick = {/* viewModel.updateUserDataDB()*/ }) {
                        Text("Save")
                    }
                },

                )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + paddingValues.calculateBottomPadding()
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            // Organization Name

            IconWithLabel(Icons.Default.AccountBox, "Choose from contacts") {

            }
            TextFieldWithLabel("Name", clientDetailState.name) { name ->
                viewModel.updateField { it.copy(name = name) }
            }


            // Billing Address Section
            SectionTitle("Billing Address")
            TextFieldWithLabel("Country", clientDetailState.country) { country ->
                viewModel.updateField { it.copy(country = country) }
            }
            TextFieldWithLabel("Street", clientDetailState.street) { street ->
                viewModel.updateField { it.copy(street = street) }
            }
            TextFieldWithLabel("Apt, Suite", clientDetailState.suite) { suite ->
                viewModel.updateField { it.copy(suite = suite) }
            }
            TextFieldWithLabel("Postal Code", clientDetailState.postalCode) { postalCode ->
                viewModel.updateField { it.copy(postalCode = postalCode) }
            }
            TextFieldWithLabel("City", clientDetailState.city) { city ->
                viewModel.updateField { it.copy(city = city) }
            }


            // Contact Details Section
            SectionTitle("Contact Details")
            TextFieldWithLabel(
                "Contact Person",
                clientDetailState.contactPerson
            ) { contactPerson ->
                viewModel.updateField { it.copy(contactPerson = contactPerson) }
            }
            TextFieldWithLabel("Phone", clientDetailState.phone) { phone ->
                viewModel.updateField { it.copy(phone = phone) }
            }
            TextFieldWithLabel("Cell", clientDetailState.cell) { cell ->
                viewModel.updateField { it.copy(cell = cell) }
            }
            TextFieldWithLabel("Email", clientDetailState.email) { email ->
                viewModel.updateField { it.copy(email = email) }
            }

            if (loadingState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}, // Prevent interactions
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            MyAlertDialog(
                shouldShowDialog = shouldShowDialog,
                errorMessage = customDialogMessage.value,
                onDismiss = {
                    shouldShowDialog.value = false

                }
            )
        }
    }


}