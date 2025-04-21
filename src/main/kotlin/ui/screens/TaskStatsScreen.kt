package ui.screens

import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import state.AppState
import utils.StatsCalculator
import model.TaskStats
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import java.text.SimpleDateFormat
import java.util.*

class TaskStatsScreen(private val appState: AppState) : Screen {
    @Composable
    override fun Content() {
        AnimatedScreen {

            val stats = remember { StatsCalculator.calculateTasksStats(appState.events) }
            val navigator = LocalNavigator.currentOrThrow

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navigator.pop() }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Task Statistics", style = MaterialTheme.typography.h4)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(stats.sortedByDescending { it.totalRuntime }) { stat ->
                        TaskStatItem(stat)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun TaskStatItem(stat: TaskStats) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(stat.name, style = MaterialTheme.typography.h6)
            Text("Priority: ${stat.priority ?: "N/A"}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Total runtime: ${formatTime(stat.totalRuntime)}")
                Text("Average runtime: ${formatTime(stat.averageRuntime)}")
            }
            Column {
                Text("Max runtime: ${formatTime(stat.maxRuntime)}")
                Text("Min runtime: ${formatTime(stat.minRuntime)}")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Context switches: ${stat.switchCount}")
        Text("Stack size: ${stat.stackSize ?: "N/A"}")
        Text("Created at: ${SimpleDateFormat("HH:mm:ss.SSS").format(Date(stat.creationTime))}")
    }
}

fun formatTime(millis: Long): String {
    return when {
        millis < 1000 -> "$millis ms"
        else -> "${millis / 1000}.${millis % 1000 / 100} s"
    }
}