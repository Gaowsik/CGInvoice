package com.example.cginvoice.presentaion

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopBarConfig(
    val title: String,
    val actions: @Composable RowScope.() -> Unit = {}
)