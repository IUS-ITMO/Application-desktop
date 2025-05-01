package state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import model.Event
import model.RealtimeConfig
import model.RealtimeEventBuffer
import utils.RealtimeClient

class AppState(initialEvents: List<Event>) {
    var events by mutableStateOf(initialEvents)
    var errorMessage by mutableStateOf<String?>(null)

    var selectedEventType by mutableStateOf<Int?>(null)
    var selectedTaskName by mutableStateOf<String?>(null)
    var selectedTimeRange by mutableStateOf<Pair<Long, Long>?>(null)
    var selectedCoreId by mutableStateOf<Int?>(null)

    private val scope = CoroutineScope(Dispatchers.Swing+ SupervisorJob())
    val realtimeBuffer = RealtimeEventBuffer(1000)
    private var realtimeClient: RealtimeClient? = null
    var isRealtimeMode by mutableStateOf(false)
        private set

    fun startRealtimeMode(url: String) {
        if (isRealtimeMode) return

        realtimeClient = RealtimeClient(
            config = RealtimeConfig(websocketUrl = url),
            buffer = realtimeBuffer,
            scope = scope
        ).apply {
            connect()
        }

        isRealtimeMode = true
    }

    fun stopRealtimeMode() {
        scope.launch {
            realtimeClient?.disconnect()
            realtimeClient = null
            isRealtimeMode = false
        }
    }
//    var searchQuery by mutableStateOf("")
}