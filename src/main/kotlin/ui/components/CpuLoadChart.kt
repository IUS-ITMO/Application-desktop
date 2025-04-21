package ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.CpuLoadPoint

@Composable
fun CpuLoadChart(loadPoints: List<CpuLoadPoint>) {
    if (loadPoints.isEmpty()) {
        Text("No CPU load data available")
        return
    }

    val minTime = loadPoints.minOf { it.time }
    val maxTime = loadPoints.maxOf { it.time }
    val timeRange = maxTime - minTime

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val padding = 16.dp.toPx()
        val chartHeight = size.height - 2 * padding
        val chartWidth = size.width - 2 * padding

        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding),
            end = Offset(padding, padding + chartHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding + chartHeight),
            end = Offset(padding + chartWidth, padding + chartHeight),
            strokeWidth = 2f
        )

        for (i in 0..10) {
            val y = padding + chartHeight * (1 - i.toFloat() / 10)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(padding, y),
                end = Offset(padding + chartWidth, y),
                strokeWidth = 1f
            )

            drawContext.canvas.nativeCanvas.apply {
                val textLayoutResult = textMeasurer.measure(
                    text = "${(i * 10).toFloat()}%",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
                drawText(
                    textLayoutResult,
                    topLeft = Offset(padding - 30.dp.toPx(), y - textLayoutResult.size.height / 2)
                )
            }
        }

        var prevPoint: Offset? = null
        loadPoints.forEach { point ->
            val x = padding + ((point.time - minTime).toFloat() / timeRange) * chartWidth
            val y = padding + chartHeight * (1 - point.load)

            val currentPoint = Offset(x, y)
            if (prevPoint != null) {
                drawLine(
                    color = Color.Blue,
                    start = prevPoint!!,
                    end = currentPoint,
                    strokeWidth = 2f
                )
            }

            drawCircle(
                color = Color.Blue,
                center = currentPoint,
                radius = 3.dp.toPx()
            )

            prevPoint = currentPoint
        }
    }
}