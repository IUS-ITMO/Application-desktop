package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import state.AppState

@Composable
fun EventList(appState: AppState) {
    val filteredEvents = appState.events.filter { event ->
        (appState.selectedEventType == null || appState.selectedEventType == event.event_code) &&
                (appState.selectedTaskName.isNullOrEmpty() || event.name.contains(appState.selectedTaskName ?: "", ignoreCase = true)) &&
                (appState.selectedTimeRange == null ||
                        (event.server_time in appState.selectedTimeRange!!.first..appState.selectedTimeRange!!.second))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredEvents) { event ->
            EventItem(event)
        }
    }
}
