package com.example.cginvoice.utills

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.example.cginvoice.data.APIResource
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun parseErrors(failure: APIResource.Error): String {
    return when {
        failure.isNetworkError -> "Network Error"
        failure.errorCode == 403 -> {
            "Unauthorized request"
        }

        failure.errorCode == 404 -> {
            ("Resource not found")
        }

        failure.errorCode == 422 -> {
            ("Validation error")
        }

        failure.errorCode == 500 -> {
            try {
                val errorBody =
                    Gson().fromJson(failure.errorBody?.string(), JsonObject::class.java)
                (errorBody.get("message").asString)
            } catch (e: Exception) {
                ("Internal server error")
            }
        }

        failure.errorCode == 504 -> {
            ("Gateway timeout")
        }

        failure.errorCode == 0 -> {
            ("Unknown error")
        }

        else -> {
            val error = failure.errorBody?.string().toString()
            (error)
        }
    }
}

inline fun <reified T> T.toJson(): String {
    return Gson().toJson(this, T::class.java)
}

// Extension function to convert a JSON string to an object
inline fun <reified T> String.fromJson(): T {
    val type = object : TypeToken<T>() {}.type
    return Gson().fromJson(this, type)
}

enum class SyncType(val type: String) {
    USER("user"),
    ADDRESS("address"),
    CONTACT("contact"),
    CLIENT("client"),
    ITEM("item"),
    INVOICE("invoice"),
    INVOICE_ITEM("invoice_item"),
    PAYMENT("payment"),

}

enum class SyncStatus(val status: String) {
    PENDING("pending"), COMPLETED("completed"), DELETE("delete")
}

inline fun <reified T> String.fromJsonList(): List<T> {
    return Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)
}

@OptIn(ExperimentalTime::class)
fun todayMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timeInMills: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return Instant.ofEpochMilli(timeInMills)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

@Composable
fun DecimalTextField(
    label: String,
    value: String,
    placeholder: String = "0.00",
    onValueChange: (Double?) -> Unit
) {
    var text by remember { mutableStateOf(value) }

    TextFieldWithLabel(label, text, KeyboardType.Decimal, placeholder) { input ->
        // Allow only digits and decimal point
        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
            text = input
            val number = input.toDoubleOrNull()
            onValueChange(number)
        }
    }
}
