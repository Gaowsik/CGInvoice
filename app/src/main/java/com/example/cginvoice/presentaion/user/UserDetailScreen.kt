package com.example.cginvoice.presentaion.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkManager
import com.example.cginvoice.data.repository.user.UserRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(viewModel: UserViewModel) {
    val userDetailState by viewModel.userDetailState.collectAsState()

    LaunchedEffect(key1 = true, block = {
        // we will get the student details when ever the screen is created
// Launched effect is a side effect
        viewModel.getUserInfo("huihuk")
    })


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sankaraa") },
                actions = {
                    TextButton(onClick = { /* Save logic */ }) {
                        Text("Save", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Organization Name
            TextFieldWithLabel("Name", userDetailState.name) {name->
                viewModel.updateField { it.copy(name = name) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Billing Address Section
            SectionTitle("Billing Address")
            TextFieldWithLabel("Country", userDetailState.country) { country->
                viewModel.updateField { it.copy(country = country) } }
            TextFieldWithLabel("Street", userDetailState.street) {street->
                viewModel.updateField { it.copy(street = street) } }
            TextFieldWithLabel("Apt, Suite", userDetailState.suite) {suite->
                viewModel.updateField { it.copy(suite = suite) }  }
            TextFieldWithLabel("Postal Code", userDetailState.postalCode) { postalCode->
                viewModel.updateField { it.copy(postalCode = postalCode) }  }
            TextFieldWithLabel("City", userDetailState.city) { city->
                viewModel.updateField { it.copy(city = city) }  }

            Spacer(modifier = Modifier.height(16.dp))

            // Identification Section
            SectionTitle("Identification")
            TextFieldWithLabel("Business ID", userDetailState.businessId) { businessId->
                viewModel.updateField { it.copy(businessId = businessId) }}

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Details Section
            SectionTitle("Contact Details")
            TextFieldWithLabel("Contact Person", userDetailState.contactPerson) {contactPerson->
                viewModel.updateField { it.copy(contactPerson = contactPerson) } }
            TextFieldWithLabel("Phone", userDetailState.phone) {phone->
                viewModel.updateField { it.copy(phone = phone) } }
            TextFieldWithLabel("Cell", userDetailState.cell) {cell->
                viewModel.updateField { it.copy(cell = cell) } }
            TextFieldWithLabel("Email", userDetailState.email) {email->
                viewModel.updateField { it.copy(email = email) }}
            TextFieldWithLabel("Website", userDetailState.website) {website->
                viewModel.updateField { it.copy(website = website) } }

            Spacer(modifier = Modifier.height(16.dp))


            TextFieldWithIconLabel(Icons.Default.Person, "Logo") {

            }

            TextFieldWithIconLabel(Icons.Default.Edit, "Signature") {

            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color.Gray,
        modifier = Modifier.padding(all = 8.dp)
    )
}

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                // Draw top stroke
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.5.dp.toPx()
                )
                // Draw bottom stroke
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawContent() // Draw the inner content
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(2f)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .padding(8.dp)
                    .weight(3f)
            )
        }
    }
}

@Composable
fun TextFieldWithIconLabel(
    icon: ImageVector, // Use an ImageVector for the icon
    label: String,
    onValueChange: (TextFieldValue) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                // Draw top stroke
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.5.dp.toPx()
                )
                // Draw bottom stroke
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawContent() // Draw the inner content
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Section
            Icon(
                imageVector = icon,
                contentDescription = "Label Icon",
                modifier = Modifier
                    .size(40.dp) // Set a fixed size for the icon
                    .padding(8.dp),
                tint = Color.Gray // Adjust color if needed
            )

            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

