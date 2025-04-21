package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventDetailScreen(private val event: Event) : Screen {
    @Composable
    override fun Content() {
        AnimatedScreen {
            val navigator = LocalNavigator.currentOrThrow

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navigator.pop() }) {
                        Text("Back")
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Event Details", style = MaterialTheme.typography.h4)
                }

                Spacer(Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Basic Info", style = MaterialTheme.typography.h6)
                        Divider(Modifier.padding(vertical = 8.dp))

                        InfoRow("Event ID", event.id.toString())
                        InfoRow("Type", "${event.eventType} (${event.event_code})")
                        InfoRow("Task Name", event.name)
                        InfoRow("Timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date(event.server_time)))

                        when(event.event_code) {
                            2 -> {
                                InfoRow("Priority", event.priority?.toString() ?: "N/A")
                                InfoRow("Stack Size", event.stack_size?.toString() ?: "N/A")
                                InfoRow("Static", event.is_static?.toString() ?: "N/A")
                            }
                            3 -> {
                                InfoRow("Core ID", event.core_id?.toString() ?: "N/A")
                            }
                        }
                    }
                }

                if (event.params.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Additional Parameters", style = MaterialTheme.typography.h6)
                            Divider(Modifier.padding(vertical = 8.dp))

                            event.params.forEach { (key, value) ->
                                InfoRow(key, value)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.body1)
    }
}