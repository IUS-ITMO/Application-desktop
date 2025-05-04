package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import model.Event
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface

// Генерация карты очередей
fun generateQueueMap(events: List<Event>): Map<String, MutableList<Pair<Long, Int>>> {
    val queueMap = mutableMapOf<String, MutableList<Pair<Long, Int>>>() // name -> list of (time, size)
    val currentSizes = mutableMapOf<String, Int>()
    for (event in events) {

        val name = event.name
        val time = event.server_time
        val size = event.size ?: 0
        val success = event.success

        when (event.event_code) {
            6 -> {
                queueMap[name] = mutableListOf(time to 0)
                currentSizes[name] = 0
            }
            7 -> if (success == 1) {
                val newSize = (currentSizes[name] ?: 0) + 1
                currentSizes[name] = newSize
                queueMap.getOrPut(name) { mutableListOf() }.add(time to newSize)
            }
            8 -> if (success == 1) {
                val newSize = maxOf((currentSizes[name] ?: 1) - 1, 0)
                currentSizes[name] = newSize
                queueMap.getOrPut(name) { mutableListOf() }.add(time to newSize)
            }
//            6 -> {
//                queueMap.putIfAbsent(name, mutableListOf())
//            }
//            7 -> {
//                if (success == 1) {
//                    queueMap.computeIfAbsent(name) { mutableListOf() }
//                        .add(time to ((size ?: 0) + 1))
//                }
//            }
//            8 -> {
//                if (success == 1) {
//                    queueMap.computeIfAbsent(name) { mutableListOf() }
//                        .add(time to ((size ?: 0) - 1))
//                }
//            }
        }
    }

    return queueMap
}
@Composable
fun QueueChar(events: List<Event>) {
    val queueMap = generateQueueMap(events)
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(verticalScrollState)
    ) {
        queueMap.forEach { (queueName, points) ->
            Text(
                "Очередь: $queueName",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .padding(bottom = 4.dp)
            )
            points.forEach { (time, size) ->
                Text("  Время: $time, Размер: $size")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}
@Composable
fun QueueChart(events: List<Event>) {
    val queueMap = generateQueueMap(events)

    val allPoints = queueMap.values.flatten()
    val minTime = allPoints.minOf { it.first }
    val maxTime = allPoints.maxOf { it.first }
    val maxSize = allPoints.maxOf { it.second }

    val padding = 60f

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width - padding
        val height = size.height - padding

        // рисуем оси
        drawLine(Color.Black, Offset(padding, 0f), Offset(padding, height), strokeWidth = 2f)
        drawLine(Color.Black, Offset(padding, height), Offset(size.width, height), strokeWidth = 2f)

        // рисуем сетку по Y
        for (i in 0..maxSize) {
            val y = height - i * (height / maxSize)
            drawLine(Color.LightGray, Offset(padding, y), Offset(size.width, y), strokeWidth = 1f)
            drawContext.canvas.nativeCanvas.drawString(
                i.toString(),
                5f,
                y,
                Font(FontMgr.default.matchFamilyStyle("Arial", FontStyle.NORMAL), 12f),
                Paint().apply { color = 0xFF000000.toInt() }
            )
        }

        // сетка по X (время)
        val timeStep = (maxTime - minTime) / 5
        for (i in 0..5) {
            val time = minTime + i * timeStep
            val x = padding + (time - minTime).toFloat() / (maxTime - minTime) * width

            // Нарисовать вертикальную линию
            drawLine(Color.LightGray, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)

            // Перевод времени из миллисекунд в секунды
            val seconds = (time - minTime) / 1000.0
            val label = String.format("%.3f s", seconds)

            // Подпись на оси X
            drawContext.canvas.nativeCanvas.drawString(
                label,
                x,
                height + 15f,
                Font(FontMgr.default.matchFamilyStyle("Arial", FontStyle.NORMAL), 12f),
                Paint().apply { color = 0xFF000000.toInt() }
            )
        }

        // рисуем линии графика
        val colors = listOf(Color.Blue, Color.Red, Color.Green, Color.Magenta)
        var index = 0
        for ((_, points) in queueMap) {
            val color = colors[index++ % colors.size]
            val offsets = points.map { (time, size) ->
                val x = padding + (time - minTime).toFloat() / (maxTime - minTime) * width
                val y = height - (size.toFloat() / maxSize) * height
                Offset(x, y)
            }

            offsets.zipWithNext { a, b ->
                drawLine(color, a, b, strokeWidth = 2f)
                drawCircle(color, radius = 4f, center = a)
            }
            offsets.lastOrNull()?.let { drawCircle(color, radius = 4f, center = it) }
        }

    }
}




