package com.example.cginvoice.utills

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeProvider {

    fun getCurrentFormattedDate(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }
}