package ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import state.AppState
import ui.screens.EventDetailScreen

@Composable
fun EventList(appState: AppState) {
    val navigator = LocalNavigator.currentOrThrow
    val filteredEvents = appState.events.filter { event ->
        (appState.selectedEventType == null || appState.selectedEventType == event.event_code) &&
                (appState.selectedTaskName.isNullOrEmpty() || event.name.contains(appState.selectedTaskName ?: "", ignoreCase = true)) &&
                (appState.selectedTimeRange == null || (event.server_time in appState.selectedTimeRange!!.first..appState.selectedTimeRange!!.second)) &&
                (appState.selectedCoreId == null || event.core_id == appState.selectedCoreId)
        (appState.searchQuery.isEmpty() ||
                event.name.contains(appState.searchQuery, ignoreCase = true) ||
                event.eventType.contains(appState.searchQuery, ignoreCase = true))
    }

    LazyColumn {
        items(filteredEvents) { event ->
            EventItem(event) { clickedEvent ->
                navigator.push(EventDetailScreen(clickedEvent))
            }
        }
    }
}
