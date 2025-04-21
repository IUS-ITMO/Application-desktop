package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import state.AppState
import ui.components.CpuLoadChart
import utils.StatsCalculator

class CpuLoadScreen(private val appState: AppState) : Screen {
    @Composable
    override fun Content() {
        AnimatedScreen {
            val loadPoints = remember {
                StatsCalculator.calculateCpuLoad(appState.events)
            }
            val navigator = LocalNavigator.currentOrThrow

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { navigator.pop() }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("CPU Load", style = MaterialTheme.typography.h4)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("CPU Load Over Time", style = MaterialTheme.typography.h6)
                CpuLoadChart(loadPoints)

                Spacer(modifier = Modifier.height(16.dp))

                val avgLoad = if (loadPoints.isNotEmpty()) {
                    loadPoints.map { it.load }.average() * 100
                } else {
                    0.0
                }
                Text("Average CPU Load: ${"%.1f".format(avgLoad)}%")
            }
        }
    }
}