package com.example.cginvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cginvoice.presentaion.user.UserViewModel
import com.example.cginvoice.ui.theme.CGInvoiceTheme
import com.example.cginvoice.utills.Constants.sampleAddress
import com.example.cginvoice.utills.Constants.sampleContact
import com.example.cginvoice.utills.Constants.sampleUser
import com.example.cginvoice.utills.Constants.sampleUserInfoResponse
import com.parse.Parse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
      //  userViewModel.insertUserRemote(sampleUser, sampleContact, sampleAddress)
        userViewModel.getUserInfo("jNCK5iHKYl")
        userViewModel.updateUserInfo(sampleUserInfoResponse)
        setContent {
            CGInvoiceTheme {

            }
        }
    }
}


