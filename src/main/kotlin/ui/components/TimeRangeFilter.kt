package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import state.AppState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimeRangeFilter(appState: AppState) {
    var showDialog by remember { mutableStateOf(false) }
    val timeRangeStr = remember(appState.selectedTimeRange) {
        if (appState.selectedTimeRange == null) "All time"
        else {
            val (start, end) = appState.selectedTimeRange!!
            "${SimpleDateFormat("HH:mm:ss").format(Date(start))} — ${
                SimpleDateFormat("HH:mm:ss").format(Date(end))
            }"
        }
    }

    Column {
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Time range: $timeRangeStr")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select time range") },
                text = {
                    Column {
                        val minTime = appState.events.minOfOrNull { it.server_time } ?: 0L
                        val maxTime = appState.events.maxOfOrNull { it.server_time } ?: 0L

                        var sliderRange by remember { mutableStateOf(0f..1f) }
                        val currentStart = remember(sliderRange) {
                            minTime + (sliderRange.start * (maxTime - minTime)).toLong()
                        }
                        val currentEnd = remember(sliderRange) {
                            minTime + (sliderRange.endInclusive * (maxTime - minTime)).toLong()
                        }

                        Text("From: ${SimpleDateFormat("HH:mm:ss.SSS").format(Date(currentStart))}")
                        Text("To: ${SimpleDateFormat("HH:mm:ss.SSS").format(Date(currentEnd))}")

                        RangeSlider(
                            value = sliderRange,
                            onValueChange = { sliderRange = it },
                            valueRange = 0f..1f,
                            steps = 100,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Button(
                            onClick = {
                                appState.selectedTimeRange = currentStart to currentEnd
                                showDialog = false
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Apply")
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}