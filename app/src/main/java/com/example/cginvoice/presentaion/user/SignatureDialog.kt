package com.example.cginvoice.presentaion.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun SignatureDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onGalleryClicked: () -> Unit = {},
    onSignatureClicked: () -> Unit = {}
) {

    Dialog(onDismissRequest = onDismiss) {

        Box(modifier = Modifier.background(Color.White)) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {

                Text(
                    text = "Message",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .background(Color.Gray)
                        .fillMaxWidth()
                        .padding(16.dp),
                )

                Text(
                    text = "From Gallery",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clickable {
                            onGalleryClicked()
                        }
                )
                Divider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Manual Signature",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clickable {
                            onSignatureClicked()
                        }
                )


            }


        }
    }
}