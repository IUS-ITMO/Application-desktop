package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Event
import utils.StatsCalculator
import utils.TimeUtils

@Composable
fun EventStats(events: List<Event>) {
    val stats = remember { StatsCalculator.calculateTasksStats(events) }
    val cpuLoad = remember { StatsCalculator.calculateCpuLoad(events) }
    val avgLoad = if (cpuLoad.isNotEmpty()) cpuLoad.map { it.load }.average() * 100 else 0.0

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text("System Statistics:", style = MaterialTheme.typography.h6)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Total tasks: ${stats.size}")
                Text("Context switches: ${events.count { it.event_code == 3 }}")
            }
            Column {
                Text("CPU Load: ${"%.1f".format(avgLoad)}%")
                Text("System uptime: ${
                    TimeUtils.formatDuration(
                    events.last().server_time - events.first().server_time
                )}")
            }
        }

        Text("Top tasks by runtime:", style = MaterialTheme.typography.subtitle2)
        stats.sortedByDescending { it.totalRuntime }.take(3).forEach { task ->
            Text(
                "${task.name}: ${TimeUtils.formatDuration(task.totalRuntime)}",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
