package com.example.cginvoice.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Shark =Color(android.graphics.Color.parseColor("#1A1A1D"))
val LividBrown =Color(android.graphics.Color.parseColor("#3B1C32"))
val TawnyPort = Color(android.graphics.Color.parseColor("#6A1E55"))
val Cadillac = Color(android.graphics.Color.parseColor("#A64D79"))



val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Catskill =Color(android.graphics.Color.parseColor("#F8FAFC"))
val HawkesBlue =Color(android.graphics.Color.parseColor("#D9EAFD"))
val PigeonPost = Color(android.graphics.Color.parseColor("#BCCCDC"))
val GullGray = Color(android.graphics.Color.parseColor("#9AA6B2"))

sealed class ThemeColors(
    val bacground: Color,
    val surafce: Color,
    val primary: Color,
    val text: Color
)  {
    object Night: ThemeColors(
        bacground = Shark,
        surafce = LividBrown,
        primary = PigeonPost,
        text = Color.White
    )
    object Day: ThemeColors(
        bacground = Catskill,
        surafce = HawkesBlue,
        primary = GullGray,
        text = Color.Black
    )
}