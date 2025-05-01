package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import state.AppState

@Composable
fun RealtimeControl(appState: AppState) {
    var websocketUrl by remember { mutableStateOf("ws://localhost:18080/trace") }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column {
        if (appState.isRealtimeMode) {
            Button(
                onClick = {
                    scope.launch {
                        appState.stopRealtimeMode()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Icon(Icons.Default.Edit, "Stop")
                Text(" Stop Realtime")
            }
        } else {
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Icon(Icons.Default.PlayArrow, "Start")
                Text(" Start Realtime")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Realtime Connection") },
                text = {
                    Column {
                        TextField(
                            value = websocketUrl,
                            onValueChange = { websocketUrl = it },
                            label = { Text("WebSocket URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Example: ws://your-esp32-ip:8080/events", style = MaterialTheme.typography.caption)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        appState.startRealtimeMode(websocketUrl)
                        showDialog = false
                    }) {
                        Text("Connect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}