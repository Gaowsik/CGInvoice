package com.example.cginvoice.presentaion.client

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cginvoice.presentaion.TopBarConfig
import com.example.cginvoice.presentaion.user.SectionTitle
import com.example.cginvoice.utills.IconWithLabel
import com.example.cginvoice.utills.MyAlertDialog
import com.example.cginvoice.utills.PermissionDialog
import com.example.cginvoice.utills.PermissionTextProvider
import com.example.cginvoice.utills.TextFieldWithLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    clientId: Int,
    topBarConfig: (TopBarConfig) -> Unit
) {
    val clientDetailState by viewModel.clientDetailState.collectAsState()
    val loadingState by viewModel.isLoading.collectAsState()
    val shouldShowDialog = remember { mutableStateOf(false) }
    val isContactPermissionDeclined = remember { mutableStateOf(false) }
    val customDialogMessage = remember { mutableStateOf("") }
    val shouldShowSaveDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(key1 = viewModel.isSaved) {
        viewModel.isSaved.collect { isSaved ->
            if (isSaved) {
                customDialogMessage.value = "Data is Saved Successfully"
                shouldShowSaveDialog.value = true
            }
        }
    }


    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { uri ->
            uri?.let {
                val resolver = context.contentResolver

                val cursor = resolver.query(it, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val name =
                            c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        val contactId =
                            c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                        // Optional: Fetch phone number
                        val hasPhoneNumber =
                            c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                        var phone: String? = null
                        if (hasPhoneNumber) {
                            val phoneCursor = resolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(contactId),
                                null
                            )
                            phoneCursor?.use { pc ->
                                if (pc.moveToFirst()) {
                                    phone = pc.getString(
                                        pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    )
                                }
                            }
                        }


                        // Update viewModel with contact data
                        viewModel.updateField {
                            it.copy(
                                name = name,
                                phone = phone ?: ""
                            )
                        }
                    }
                }
            }
        }
    )

    val contactPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                pickContactLauncher.launch(null)
            }
            isContactPermissionDeclined.value = !it
        })

    LaunchedEffect(key1 = true, block = {
        if (clientId != -1) {
            viewModel.getClientByClientId(clientId)
        }
    })

    topBarConfig(
        TopBarConfig("Add Client", actions = {
            TextButton(onClick = { viewModel.updateClientData() }) {
                Text("Save")
            }
        })
    )


        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            // Organization Name

            IconWithLabel(Icons.Default.AccountBox, "Choose from contacts") {
                contactPermissionResultLauncher.launch(Manifest.permission.READ_CONTACTS)
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
                    navController.popBackStack()

                }
            )

            if (activity != null) {
                PermissionDialog(
                    shouldShowDialog = isContactPermissionDeclined,
                    permission = ContactPermissionTextProvider(),
                    isPermanentlyDeclined = !activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS),
                    onDismiss = {
                        isContactPermissionDeclined.value = false
                    },
                    onOkClick = {
                        isContactPermissionDeclined.value = false
                        contactPermissionResultLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    onGoToAppSettingsClick = {
                        isContactPermissionDeclined.value = false
                        activity.openAppSettings()
                    }
                )
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
    }


class ContactPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined contact permission. " + "you can go to the app setting to grant it"
        } else {
            "this app needs access to your contacts"
        }
    }

}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

