package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Event

@Composable
fun EventStats(events: List<Event>) {
    val taskCount = events.distinctBy { it.name }.size
    val contextSwitches = events.count { it.event_code == 3 }
    val createdTasks = events.count { it.event_code == 2 }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text("System Statistics:", style = MaterialTheme.typography.h6)
        Text("Total tasks: $taskCount")
        Text("Context switches: $contextSwitches")
        Text("Tasks created: $createdTasks")
    }
}