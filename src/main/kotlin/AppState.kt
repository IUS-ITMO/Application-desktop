import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppState {
    var taskEvents by mutableStateOf<List<FreeRTOSEvent>>(emptyList())
        private set

    fun addEvent(event: FreeRTOSEvent) {
        taskEvents = taskEvents + event
    }
}