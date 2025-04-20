package state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Event

class AppState(initialEvents: List<Event>) {
    var events by mutableStateOf(initialEvents)

    var errorMessage by mutableStateOf<String?>(null)

    var selectedEventType by mutableStateOf<Int?>(null)
    var selectedTaskName by mutableStateOf<String?>(null)
    var selectedTimeRange by mutableStateOf<Pair<Long, Long>?>(null)
}
