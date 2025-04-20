package ui.components

import androidx.compose.foundation.border
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
fun EventItem(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), MaterialTheme.shapes.medium)
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
