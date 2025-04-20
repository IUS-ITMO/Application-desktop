package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Event

@Composable
fun EventItem(event: Event, onClick: (Event) -> Unit = {}) {
    val taskColors = mapOf(
        "idle" to Color(0xFF4CAF50),
        "myTask" to Color(0xFF2196F3),
        "myTask-2" to Color(0xFFFF9800),
        "default" to MaterialTheme.colors.primary
    )

    val bgColor = taskColors[event.name] ?: taskColors["default"]!!
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(event) }
            .padding(8.dp)
            .background(
                color = bgColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = bgColor.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Event #${event.id}", style = MaterialTheme.typography.subtitle1)
            Text(
                event.eventType,
                color = when(event.event_code) {
                    1 -> MaterialTheme.colors.primary
                    2 -> MaterialTheme.colors.secondary
                    3 -> Color(0xFF4CAF50)
                    4 -> MaterialTheme.colors.error
                    else -> MaterialTheme.colors.onSurface
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("Task: ${event.name}", style = MaterialTheme.typography.body1)
        Text("Time: ${event.server_time}", style = MaterialTheme.typography.body2)

        when(event.event_code) {
            2 -> Column {
                Text("Priority: ${event.priority ?: "N/A"}")
                Text("Stack: ${event.stack_size ?: "N/A"}")
                Text("Static: ${event.is_static ?: "N/A"}")
            }
            3 -> Text("Core: ${event.core_id ?: "N/A"}")
        }
    }
}
