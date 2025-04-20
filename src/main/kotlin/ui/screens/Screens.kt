package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import state.AppState
import ui.components.EventFilter
import ui.components.EventList
import ui.components.EventStats
import ui.components.GanttChart

@Composable
fun App(appState: AppState) {
    Navigator(MainScreen1(appState))
}

class MainScreen1(private val appState: AppState) : Screen {
    @Composable
    override fun Content() {
        AnimatedScreen {
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

                EventFilter(appState)

                val navigator = LocalNavigator.currentOrThrow
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { navigator.push(TaskStatsScreen(appState)) }) {
                        Text("Task Stats")
                    }
                    Button(onClick = { navigator.push(CpuLoadScreen(appState)) }) {
                        Text("CPU Load")
                    }
                    Button(onClick = { navigator.push(ChartScreen(appState)) }) {
                        Text("Gantt Chart")
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
                    Text("Gantt chart", style = MaterialTheme.typography.h4)
                }

                GanttChart(appState.events)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}