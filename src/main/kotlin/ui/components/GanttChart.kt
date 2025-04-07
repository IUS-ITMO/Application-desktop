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
    val taskMap = mutableMapOf<String, MutableList<Pair<Long, Long>>>() // name -> list of (start, end)

    // Сортируем события по времени
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
            3 -> { // Начало выполнения задачи
                if (lastTask != null && lastEndTime != null) {
                    taskMap.computeIfAbsent(taskName) { mutableListOf() }
                        .add(lastEndTime to eventTime)
                }
                lastTask = taskName
                lastEndTime = eventTime
            }
//            4 -> { // Завершение задачи
//                if (lastTask == taskName && lastEndTime != null) {
//                    taskMap.computeIfAbsent(taskName) { mutableListOf() }
//                        .add(lastEndTime!! to eventTime)
//                }
//            }
        }
    }

//    for ((task, intervals) in taskMap) {
//        println("Task: $task")
//        for ((start, end) in intervals) {
//            println("Start: $start, End: $end")
//        }
//    }
//    for ((task, startTime) in activeTasks) {
//        taskMap.computeIfAbsent(task) { mutableListOf() }
//            .add(startTime to System.currentTimeMillis())
//    }

    return taskMap
}

@Composable
fun GanttChart(events: List<Event>) {
    val taskMap = generateTaskMap(events)

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
        // Колонка с названиями задач
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
            taskMap.entries.forEachIndexed { index, (taskName, intervals) ->
                intervals.forEach { (start, end) ->
                    val startX = ((start - minTime).toFloat() / timeRange) * canvasWidth
                    val endX = ((end - minTime).toFloat() / timeRange) * canvasWidth

                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(startX, index * 35f),
                        size = Size(endX - startX, 20f)
                    )
                }
            }
        }
    }
}
