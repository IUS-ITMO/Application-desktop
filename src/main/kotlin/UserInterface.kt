import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskEventList(appState: AppState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(appState.taskEvents) { event ->
            TaskEventItem(event)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskEventItem(event: FreeRTOSEvent) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = event.taskName,
            style = MaterialTheme.typography.h1
        )
        Text(
            text = "Event: ${event.eventType}",
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "Timestamp: ${event.timestamp}",
            style = MaterialTheme.typography.body2
        )
    }
}