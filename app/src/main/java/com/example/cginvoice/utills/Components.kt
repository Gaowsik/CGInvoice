package com.example.cginvoice.utills

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cginvoice.R

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search", // Default placeholder text
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp)) // Rounded corners
            .background(Color.White) // Background color
    ) {
        Row(
            modifier = Modifier
                .padding(start = 24.dp, top = 8.dp, bottom = 8.dp, end = 8.dp) // Space for the search icon
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LocalContentColor.current.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField() // The actual text field
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(androidx.compose.ui.Alignment.CenterVertically)
            )
        }
    }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.8f))
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(2f)
            )
            BasicTextField(
                value = value,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current
                ),
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(3f)
            )
        }
    }
}

@Composable
fun MyAlertDialog(
    shouldShowDialog: MutableState<Boolean>,
    errorMessage: String,
    onDismiss: () -> Unit
) {
    if (shouldShowDialog.value) { // 2
        AlertDialog( // 3
            onDismissRequest = { // 4
                shouldShowDialog.value = false
            },
            // 5
            title = { Text(text = stringResource(id = R.string.title_alert)) },
            text = { Text(text = errorMessage) },
            confirmButton = { // 6
                Button(
                    onClick = {
                        onDismiss.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.action_confirm),
                        color = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun TextFieldWithIconLabel(
    icon: ImageVector, // Use an ImageVector for the icon
    imageUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f))
            .clickable { onClick() }
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

            AsyncImage(
                model = imageUrl, // your saved image URL
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp),
                placeholder = painterResource(R.drawable.ic_launcher_foreground), // optional
                error = painterResource(R.drawable.ic_launcher_foreground),        // optional fallback
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun IconWithLabel(
    icon: ImageVector, // Use an ImageVector for the icon
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f))
            .clickable { onClick() }
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
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(2f)
            )


        }
    }
}