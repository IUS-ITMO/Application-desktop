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
import androidx.compose.ui.platform.LocalDensity
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
    val taskNames = taskMap.keys.toList()

    val minTime = taskMap.values.flatten().minOfOrNull { it.first } ?: return
    val maxTime = taskMap.values.flatten().maxOfOrNull { it.second } ?: return
    val timeRange = maxTime - minTime
    val divisions = 12
    val showTasksAtFirstCount = 1
    val showMoreTasksCount = 1

    val rowHeightDp = 35.dp
    val barHeightDp = 20.dp
    val density = LocalDensity.current
    val rowHeightPx = with(density) { rowHeightDp.toPx() }
    val barHeightPx = with(density) { barHeightDp.toPx() }
    val sidePadding = 40.dp
    val colors = listOf(Color(0xFF4285F4), Color(0xFFEA4335), Color(0xFFFBBC05), Color(0xFF34A853))


    var visibleTasks by remember { mutableStateOf(showTasksAtFirstCount) }

    val displayedTasks = taskNames.take(visibleTasks)


    Column {
        TimeAxis(minTime, maxTime, divisions, sidePadding)

        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            drawLine(
                color = Color.Gray,
                start = Offset(140f, 0f),
                end = Offset(size.width-40, 0f),
                strokeWidth = 1f
            )
        }

        Row(Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.width(100.dp)) {
                items(displayedTasks) { taskName ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeightDp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = taskName,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (visibleTasks < taskNames.size) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { visibleTasks += showMoreTasksCount },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Gray,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Show more", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            val taskToIndex = displayedTasks.withIndex().associate { it.value to it.index }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .height(rowHeightDp * taskNames.size)
                    .padding(start = sidePadding, end = sidePadding)
            ) {
                val canvasWidth = size.width
                val taskAreaHeight = rowHeightPx * displayedTasks.size


                for (i in 0..divisions) {
                    val x = (i * (canvasWidth / divisions))
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(x, 0f),
                        end = Offset(x, taskAreaHeight),
                        strokeWidth = 1f
                    )
                }
                drawLine(Color.Gray, Offset(0f, taskAreaHeight), Offset(canvasWidth, taskAreaHeight), strokeWidth = 1f)

                taskMap.entries.forEach { (taskName, intervals) ->
                    val index = taskToIndex[taskName] ?: return@forEach

                    intervals.forEach { (start, end) ->
                        val startX = ((start - minTime).toFloat() / timeRange) * canvasWidth
                        val endX = ((end - minTime).toFloat() / timeRange) * canvasWidth
                        val yOffset = index * rowHeightPx + (rowHeightPx - barHeightPx) / 2

                        drawRoundRect(
                            color = colors[index % colors.size],
                            topLeft = Offset(startX, yOffset),
                            size = Size(endX - startX, barHeightPx),
                            cornerRadius = CornerRadius(4f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TimeAxis(minTime: Long, maxTime: Long, divisions: Int, sidePadding: Dp) {
    val axisHeight = 24.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 150.dp + sidePadding, end = sidePadding)
            .height(axisHeight)
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val stepPx = widthPx / divisions

        for (i in 0..divisions) {
            val current = minTime + (maxTime - minTime) / divisions * i
            val rel = current - minTime
            val label = "+${rel} ms"

            val offsetDp = with(LocalDensity.current) { (stepPx * i).toDp() }

            Text(
                text = label,
                fontSize = 10.sp,
                modifier = Modifier
                    .absoluteOffset(x = offsetDp, y = 0.dp)
            )
        }
    }
}
