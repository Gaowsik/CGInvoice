package com.example.cginvoice.presentaion.user

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    onDone: (Bitmap) -> Unit = {},
    onCancel: () -> Unit = {}
) {

    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentPath by remember { mutableStateOf(listOf<Offset>()) }


    Box(modifier = Modifier.background(Color.White)) {

        androidx.compose.foundation.Canvas(modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        currentPath = listOf(it)
                    },
                    onDrag = { _, dragAmount ->
                        val newPoint = currentPath.last() + dragAmount
                        currentPath = currentPath + newPoint
                    },
                    onDragEnd = {
                        paths = paths + listOf(currentPath)
                        currentPath = listOf()
                    }
                )
            }
        ) {

            // Draw all completed paths
            paths.forEach { path ->
                drawPath(
                    path = Path().apply {
                        if (path.isNotEmpty()) {
                            moveTo(path.first().x, path.first().y)
                            path.drop(1).forEach {
                                lineTo(it.x, it.y)
                            }
                        }
                    },
                    color = Color.Black,
                    style = Stroke(width = 4f)
                )
            }

            // Draw current path (active drawing)
            if (currentPath.isNotEmpty()) {
                drawPath(
                    path = Path().apply {
                        moveTo(currentPath.first().x, currentPath.first().y)
                        currentPath.drop(1).forEach { lineTo(it.x, it.y) }
                    },
                    color = Color.Black,
                    style = Stroke(width = 4f)
                )
            }

        }

        // Buttons to Save or Cancel
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(onClick = {
                // Create a bitmap and canvas to draw the paths onto
                val bitmap = Bitmap.createBitmap(1000, 600, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)

                // Create a paint object for drawing on the canvas
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 8f // Set the stroke width
                }

                // Draw all paths onto the bitmap canvas
                paths.forEach { path ->
                    val androidPath = android.graphics.Path()
                    if (path.isNotEmpty()) {
                        androidPath.moveTo(path.first().x, path.first().y)
                        path.drop(1).forEach { androidPath.lineTo(it.x, it.y) }
                        canvas.drawPath(androidPath, paint)
                    }
                }

                // Return the drawn bitmap
                onDone(bitmap)
            }) {
                Text("Save")
            }
        }

    }

}

















