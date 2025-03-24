package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import state.AppState
import ui.components.EventList
import ui.components.EventStats

@Composable
fun MainScreen(appState: AppState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        appState.errorMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { appState.errorMessage = null },
                title = { Text("Error") },
                text = { Text(message) },
                confirmButton = {
                    Button(onClick = { appState.errorMessage = null }) {
                        Text("OK")
                    }
                }
            )
        }

        Text(
            text = "FreeRTOS Task Events",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            appState.events.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (appState.errorMessage == null) {
                        CircularProgressIndicator()
                    } else {
                        Text("No data available")
                    }
                }
            }
            else -> {
                EventStats(appState.events)
                EventList(appState)
            }
        }
    }
}