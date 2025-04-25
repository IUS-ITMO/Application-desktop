package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import state.AppState
import ui.components.*

@Composable
fun App(appState: AppState) {
    Navigator(MainScreen1(appState))
}

class MainScreen1(private val appState: AppState) : Screen {
    @Composable
    override fun Content() {
        var showExportDialog by remember { mutableStateOf(false) }
        var showImportDialog by remember { mutableStateOf(false) }
        AnimatedScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showImportDialog = true }) {
                        Icon(Icons.Default.Add, "Import")
                        Text(" Import", modifier = Modifier.padding(start = 4.dp))
                    }

                    Button(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Add, "Export")
                        Text(" Export", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                if (showExportDialog) {
                    ExportDialog(
                        events = appState.events,
                        onDismiss = { showExportDialog = false },
                        onSave = { file -> }
                    )
                }

                if (showImportDialog) {
                    ImportDialog(
                        onDismiss = { showImportDialog = false },
                        onImport = { importedEvents ->
                            appState.events = importedEvents
                        }
                    )
                }
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

                RealtimeControl(appState)
                if (appState.isRealtimeMode) {
                    LiveGanttChart(appState.events)
                    Text("Live events: ${appState.events.size}",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(8.dp))
                } else {
                    EventStats(appState.events)
                    EventList(appState)
                }

                EventFilter(appState)

                val navigator = LocalNavigator.currentOrThrow
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { navigator.push(TaskStatsScreen(appState)) }) {
                        Text("Task Stats")
                    }
                    Button(onClick = { navigator.push(ChartScreen(appState)) }) {
                        Text("Task Execution Timeline")
                    }
                    Button(onClick = { navigator.push(CpuLoadScreen(appState)) }) {
                        Text("CPU Load")
                    }
                }

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
    }
}

class ChartScreen(private val appState: AppState) : Screen {
    @Composable
    override fun Content() {
        AnimatedScreen {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val navigator = LocalNavigator.currentOrThrow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { navigator.pop() }) {
                        Text("Back")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Task Execution Timeline", style = MaterialTheme.typography.h4)
                }

                GanttChart(appState.events)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}