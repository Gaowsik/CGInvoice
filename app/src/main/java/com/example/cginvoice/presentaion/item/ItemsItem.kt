package com.example.cginvoice.presentaion.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cginvoice.domain.model.client.ClientData
import com.example.cginvoice.domain.model.item.ItemData

@Composable
fun ItemsItem(itemData: ItemData, onClickListener: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClickListener(itemData.itemId) }
            .padding(top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)) {
            Text(text = itemData.itemName, style = MaterialTheme.typography.titleMedium)
        }
    }
}