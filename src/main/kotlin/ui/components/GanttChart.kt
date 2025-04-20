package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import model.Event

fun generateTaskMap(events: List<Event>): Map<String, MutableList<Pair<Long, Long>>> {
    val taskMap = mutableMapOf<String, MutableList<Pair<Long, Long>>>()

    var lastTask: String? = null
    var lastEndTime: Long? = null

    for (event in events) {
        val taskName = event.name
        val eventTime = event.server_time

        when (event.event_code) {
            2 -> {
                lastTask = taskName
                lastEndTime = eventTime
                taskMap.putIfAbsent(event.name, mutableListOf())
            }
            3 -> {
                if (lastTask != null && lastEndTime != null) {
                    taskMap.computeIfAbsent(taskName) { mutableListOf() }
                        .add(lastEndTime to eventTime)
                }
                lastTask = taskName
                lastEndTime = eventTime
            }
        }
    }

    return taskMap
}

@Composable
fun GanttChart(events: List<Event>) {
    val taskMap = generateTaskMap(events)
    val colors = listOf(Color(0xFF4285F4), Color(0xFFEA4335), Color(0xFFFBBC05), Color(0xFF34A853))

    val minTime = taskMap.values.flatten().minOfOrNull { it.first } ?: return
    val maxTime = taskMap.values.flatten().maxOfOrNull { it.second } ?: return
    val timeRange = maxTime - minTime

    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp).padding(top = 8.dp)) {
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1f
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.width(150.dp)
        ) {
            items(taskMap.keys.toList()) { taskName ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)

                ) {
                    Text(text = taskName, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width

            drawLine(
                color = Color.Gray,
                start = Offset(10f, 0f),
                end = Offset(10f, 20f),
                strokeWidth = 1f
            )
            taskMap.entries.forEachIndexed { index, (_, intervals) ->
                intervals.forEach { (start, end) ->
                    val startX = ((start - minTime).toFloat() / timeRange) * canvasWidth
                    val endX = ((end - minTime).toFloat() / timeRange) * canvasWidth

                    drawRoundRect(
                        color = colors[index % colors.size],
                        topLeft = Offset(startX, index * 35f),
                        size = Size(endX - startX, 30f),
                        cornerRadius = CornerRadius(4f)
                    )
                }
            }
        }
    }
}
