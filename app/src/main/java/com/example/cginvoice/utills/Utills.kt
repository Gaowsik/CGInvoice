package com.example.cginvoice.utills

import com.example.cginvoice.data.APIResource
import com.google.gson.Gson
import com.google.gson.JsonObject

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
    return Gson().fromJson(this, T::class.java)
}

enum class SyncType(val type: String) {
    USER("user"),
    ADDRESS("address"),
    CONTACT("contact"),
    CLIENT("contact")
}
