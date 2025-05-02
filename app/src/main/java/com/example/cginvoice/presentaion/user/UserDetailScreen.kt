package com.example.cginvoice.presentaion.user

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cginvoice.R
import com.example.cginvoice.presentaion.nav.NavItem
import com.example.cginvoice.presentaion.nav.navigateToScreen
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.TextFieldWithIconLabel
import com.example.cginvoice.utills.TextFieldWithLabel

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavHostController = NavHostController(context = LocalContext.current),
    viewModel: UserViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    val userDetailState by viewModel.userDetailState.collectAsState()
    val loadingState by viewModel.isLoading.collectAsState()
    val shouldShowDialog = remember { mutableStateOf(false) }
    val shouldShowSaveDialog = remember { mutableStateOf(false) }
    var shouldSignatureDialog by remember { mutableStateOf(false) }
    val customDialogMessage = remember { mutableStateOf("") }
    var showSignaturePad by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true, block = {
        viewModel.getUserInfo()
    })

    LaunchedEffect(key1 = viewModel.isSync) {
        viewModel.isSync.collect { isSync ->
            if (isSync) {
                customDialogMessage.value = "Data synchronization is completed"
                shouldShowDialog.value = true
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

    LaunchedEffect(key1 = viewModel.errorMessage) {
        viewModel.errorMessage.collect { error ->
            if (error.isNotEmpty()) {
                customDialogMessage.value = error
                shouldShowDialog.value = true
            }
        }
    }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.uploadLogo(uri, context)
            }
        }
    )

    val signatureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.uploadSelectedSignature(uri, context)
                shouldSignatureDialog = false
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sankaraa") },
                actions = {
                    TextButton(onClick = { viewModel.updateUserDataDB() }) {
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
            TextFieldWithLabel("Name", userDetailState.name) { name ->
                viewModel.updateField { it.copy(name = name) }
            }


            // Billing Address Section
            SectionTitle("Billing Address")
            TextFieldWithLabel("Country", userDetailState.country) { country ->
                viewModel.updateField { it.copy(country = country) }
            }
            TextFieldWithLabel("Street", userDetailState.street) { street ->
                viewModel.updateField { it.copy(street = street) }
            }
            TextFieldWithLabel("Apt, Suite", userDetailState.suite) { suite ->
                viewModel.updateField { it.copy(suite = suite) }
            }
            TextFieldWithLabel("Postal Code", userDetailState.postalCode) { postalCode ->
                viewModel.updateField { it.copy(postalCode = postalCode) }
            }
            TextFieldWithLabel("City", userDetailState.city) { city ->
                viewModel.updateField { it.copy(city = city) }
            }


            // Identification Section
            SectionTitle("Identification")
            TextFieldWithLabel("Business ID", userDetailState.businessId) { businessId ->
                viewModel.updateField { it.copy(businessId = businessId) }
            }


            // Contact Details Section
            SectionTitle("Contact Details")
            TextFieldWithLabel(
                "Contact Person",
                userDetailState.contactPerson
            ) { contactPerson ->
                viewModel.updateField { it.copy(contactPerson = contactPerson) }
            }
            TextFieldWithLabel("Phone", userDetailState.phone) { phone ->
                viewModel.updateField { it.copy(phone = phone) }
            }
            TextFieldWithLabel("Cell", userDetailState.cell) { cell ->
                viewModel.updateField { it.copy(cell = cell) }
            }
            TextFieldWithLabel("Email", userDetailState.email) { email ->
                viewModel.updateField { it.copy(email = email) }
            }
            TextFieldWithLabel("Website", userDetailState.website) { website ->
                viewModel.updateField { it.copy(website = website) }
            }

            SectionTitle("Logo")

            TextFieldWithIconLabel(Icons.Default.Person, userDetailState.logo) {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            SectionTitle("Signature")

            TextFieldWithIconLabel(Icons.Default.Edit, userDetailState.signature) {
                shouldSignatureDialog = true

            }
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
        if (showSignaturePad) {
            Dialog(onDismissRequest = { showSignaturePad = false }) {
                SignaturePad(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White),
                    onDone = { bitmap ->
                        viewModel.uploadSignature(bitmap, context)
                        showSignaturePad = false
                    },
                    onCancel = {
                        showSignaturePad = false
                    }
                )
            }
        }

        if (shouldSignatureDialog) {
            SignatureDialog(
                onDismiss = { shouldSignatureDialog = false },
                onGalleryClicked = {
                    signatureLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onSignatureClicked = {
                    showSignaturePad = true
                    shouldSignatureDialog = false
                })
        }

        MyAlertDialog(
            shouldShowDialog = shouldShowDialog,
            errorMessage = customDialogMessage.value,
            onDismiss = {
                shouldShowDialog.value = false

            }
        )

        MyAlertDialog(
            shouldShowDialog = shouldShowSaveDialog,
            errorMessage = customDialogMessage.value,
            onDismiss = {
                shouldShowSaveDialog.value = false
                navController.navigateToScreen(NavItem.Invoice.path, NavItem.User.path)

            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
    )
}









